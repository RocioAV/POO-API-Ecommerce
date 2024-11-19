package ar.edu.unju.fi.poo.tp8poo.service;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import ar.edu.unju.fi.poo.tp8poo.dto.FiltroVentaDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.VentaDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;

/**
 * Servicio para exportar datos de ventas en formatos Excel y PDF.
 */
@Slf4j
@Service
public class ExportService {
    private static final String INVALIDO = "Formato invalido : {}";
    private static final String LOGO_URL = "https://firebasestorage.googleapis.com/v0/b/tp8poo2024.firebasestorage.app/o/logo%2FlogoG2.png?alt=media&token=c1ac110f-6b05-4090-851e-8986b2252aba";

    /**
     * Exporta datos de ventas a un archivo Excel.
     *
     * @param ventas      Lista de ventas a exportar.
     * @param filtroDTO   Filtros aplicados a las ventas.
     */
    public byte[] exportarAExcelComoBytes(List<VentaDTO> ventas, FiltroVentaDTO filtroDTO) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        	Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Ventas");
            agregarLogoExcel(workbook, sheet);
            agregarTituloExcel(workbook, sheet, "Filtros Aplicados: " + obtenerTitulo(filtroDTO));
            agregarEncabezadoExcel(workbook, sheet);
            agregarDatosVentasExcel(sheet, ventas, workbook);

            workbook.write(baos);
            workbook.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error al generar Excel: {}", e.getMessage());
            throw new NegocioException("Error al generar Excel: " + e.getMessage());
        }
    }


    /**
     * Exporta datos de ventas a un archivo PDF.
     *
     * @param ventas     Lista de ventas a exportar.
     * @param filtroDTO  Filtros aplicados a las ventas.
     */
    public byte[] exportarAPdfComoBytes(List<VentaDTO> ventas, FiltroVentaDTO filtroDTO) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            agregarLogoPdf(document);
            agregarTituloPdf(document, filtroDTO);
            agregarTablaPdf(document, ventas);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error al generar PDF: {}", e.getMessage());
            throw new NegocioException("Error al generar PDF: " + e.getMessage());
        }
    }
    /**
     * Exporta datos de ventas a un archivo ZIP y contiene un pdf y un excel.
     *
     * @param ventas     Lista de ventas a exportar.
     * @param filtroDTO  Filtros aplicados a las ventas.
     */
    public byte[] exportarAmbosComoZip(List<VentaDTO> ventas, FiltroVentaDTO filtroDTO, String nombreArchivo) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
             ZipOutputStream zipOut = new ZipOutputStream(baos);

            ZipEntry pdfEntry = new ZipEntry(nombreArchivo + ".pdf");
            zipOut.putNextEntry(pdfEntry);
            zipOut.write(exportarAPdfComoBytes(ventas, filtroDTO));
            zipOut.closeEntry();

            ZipEntry excelEntry = new ZipEntry(nombreArchivo + ".xlsx");
            zipOut.putNextEntry(excelEntry);
            zipOut.write(exportarAExcelComoBytes(ventas, filtroDTO));
            zipOut.closeEntry();
            
            zipOut.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error al generar ZIP: {}", e.getMessage());
            throw new NegocioException("Error al generar ZIP: " + e.getMessage());
        }
    }

    public byte[] exportarArchivo(List<VentaDTO> ventas, FiltroVentaDTO filtroDTO, String nombreArchivo, String formato) {
        byte[] archivoBytes = null;

        switch (formato.toLowerCase()) {
            case "pdf":
                archivoBytes = exportarAPdfComoBytes(ventas, filtroDTO);
                break;
            case "excel":
                archivoBytes = exportarAExcelComoBytes(ventas, filtroDTO);
                break;
            case "ambos":
                archivoBytes = exportarAmbosComoZip(ventas, filtroDTO, nombreArchivo);
                break;
            default:
                log.error("Error con el formato {}", formato);
        }

        return archivoBytes;
    }

    public HttpHeaders establecerEncabezados(String nombreArchivo, String formato) {
        String contentType;
        String extension = switch (formato.toLowerCase()) {
            case "pdf" -> {
                contentType = "application/pdf";
                yield ".pdf";
            }
            case "excel" -> {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                yield ".xlsx";
            }
            case "ambos" -> {
                contentType = "application/zip";
                yield ".zip";
            }
            default -> {
                log.error(INVALIDO, formato);
                throw new NegocioException("Formato inválido");
            }
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(nombreArchivo + extension).build());
        
        return headers;
    }

    private void agregarLogoExcel(Workbook workbook, Sheet sheet) throws IOException {
    	try { URI logoUri = new URI(LOGO_URL); 
    	BufferedImage logoImage = ImageIO.read(logoUri.toURL()); 
    	try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
    		ImageIO.write(logoImage, "png", baos); 
    		int pictureIdx = workbook.addPicture(baos.toByteArray(), Workbook.PICTURE_TYPE_PNG); 
    		Drawing<?> drawing = sheet.createDrawingPatriarch(); 
    		ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor(); 
    		anchor.setCol1(0); 
    		anchor.setRow1(0); 
    		anchor.setCol2(1); 
    		anchor.setRow2(3); 
    		drawing.createPicture(anchor, pictureIdx).resize(1); 
    		} 
    	} catch (URISyntaxException e) { 
    		throw new IOException("Error al convertir la URL del logo a URI", e); }
    }

    private void agregarTituloExcel(Workbook workbook, Sheet sheet, String titulo) {
        Row row = sheet.createRow(3);
        Cell cell = row.createCell(0);
        cell.setCellValue(titulo);

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 5));
    }

    private void agregarEncabezadoExcel(Workbook workbook, Sheet sheet) {
        String[] columnas = {"ID", "Fecha y Hora", "Precio Producto", "Forma de Pago", "Cliente", "Producto"};
        Row headerRow = sheet.createRow(4);

        CellStyle headerStyle = crearEstiloEncabezado(workbook);
        for (int i = 0; i < columnas.length; i++) {
            sheet.setColumnWidth(i, 5000);
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void agregarDatosVentasExcel(Sheet sheet, List<VentaDTO> ventas, Workbook workbook) {
        CellStyle dataStyle = crearEstiloDatos(workbook);
        int rowNum = 5;
        for (VentaDTO venta : ventas) {
        	Row row = sheet.createRow(rowNum++); 
    		Cell cell0 = row.createCell(0); 
    		cell0.setCellValue(venta.getId()); 
    		cell0.setCellStyle(dataStyle); 
    		Cell cell1 = row.createCell(1); 
    		cell1.setCellValue(venta.getFechaYHora()); 
    		cell1.setCellStyle(dataStyle); 
    		Cell cell2 = row.createCell(2); 
    		cell2.setCellValue(venta.getPrecioProducto()); 
    		cell2.setCellStyle(dataStyle); 
    		Cell cell3 = row.createCell(3); 
    		cell3.setCellValue(venta.getFormaPago()); 
    		cell3.setCellStyle(dataStyle); 
    		Cell cell4 = row.createCell(4); 
    		cell4.setCellValue(venta.getCliente().getNombre() + " " + venta.getCliente().getApellido()); 
    		cell4.setCellStyle(dataStyle); 
    		Cell cell5 = row.createCell(5); 
    		cell5.setCellValue(venta.getProducto().getNombre()); 
    		cell5.setCellStyle(dataStyle);
        }
    }

    private void agregarLogoPdf(Document document) throws IOException, DocumentException {
        try {
            URI uri = new URI(LOGO_URL);

            try (InputStream inputStream = uri.toURL().openStream()) {
                Image logo = Image.getInstance(inputStream.readAllBytes());
                logo.scaleToFit(100, 50);
                document.add(logo);
            }
        } catch (URISyntaxException e) {
            throw new IOException("Error al procesar la URL del logo: " + LOGO_URL, e);
        }
    }

    private void agregarTituloPdf(Document document, FiltroVentaDTO filtroDTO) throws DocumentException {
        Paragraph titulo = new Paragraph("Filtros Aplicados: " + obtenerTitulo(filtroDTO),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));
    }

    private void agregarTablaPdf(Document document, List<VentaDTO> ventas) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        String[] columnas = {"ID", "Fecha y Hora", "Precio Producto", "Forma de Pago", "Cliente", "Producto"};
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

        for (String columna : columnas) {
            PdfPCell cell = new PdfPCell(new Phrase(columna, headerFont));
            cell.setBackgroundColor(BaseColor.BLUE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        for (VentaDTO venta : ventas) {
            table.addCell(String.valueOf(venta.getId()));
            table.addCell(venta.getFechaYHora());
            table.addCell(String.valueOf(venta.getPrecioProducto()));
            table.addCell(venta.getFormaPago());
            table.addCell(venta.getCliente().getNombre()+" "+venta.getCliente().getApellido());
            table.addCell(venta.getProducto().getNombre());
        }

        document.add(table);
    }

    public String obtenerTitulo(FiltroVentaDTO filtroDTO) {
        StringBuilder titulo = new StringBuilder();
        if (filtroDTO.getFechaDesde() != null) titulo.append("Desde: ").append(filtroDTO.getFechaDesde()).append(" ");
        if (filtroDTO.getFechaHasta() != null) titulo.append(", Hasta: ").append(filtroDTO.getFechaHasta()).append(" ");
        if (filtroDTO.getIdCliente() != null) titulo.append(", ID Cliente: ").append(filtroDTO.getIdCliente()).append(" ");
        if (filtroDTO.getNombreCliente() != null) titulo.append(", Nombre Cliente: ").append(filtroDTO.getNombreCliente());
        return titulo.toString().trim();
    }

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle crearEstiloDatos(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
