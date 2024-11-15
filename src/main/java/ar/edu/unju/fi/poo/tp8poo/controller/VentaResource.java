package ar.edu.unju.fi.poo.tp8poo.controller;

import ar.edu.unju.fi.poo.tp8poo.controller.interfaces.IDocVentaResource;
import ar.edu.unju.fi.poo.tp8poo.dto.FiltroVentaDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.VentaDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.ExportService;
import ar.edu.unju.fi.poo.tp8poo.service.VentaService;
import ar.edu.unju.fi.poo.tp8poo.util.ConstantesMensajes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/venta")
public class VentaResource implements IDocVentaResource {
	
    private final VentaService ventaService;
    private final ExportService exportService;

    public VentaResource(VentaService ventaService,
    					 ExportService exportService) {
        this.ventaService = ventaService;
        this.exportService = exportService;
    }

    @Override
    @GetMapping("/ventas")
    public ResponseEntity<Map<String, Object>> getAllVentas(){
        log.info("/api/v1/venta/ventas");
        Map<String, Object> response = new HashMap<>();
        List<VentaDTO> ventas = ventaService.findAll();
        response.put("ventas", ventas);
        response.put(ConstantesMensajes.MENSAJE, "Ventas obtenidas con éxito");
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> agregarVenta(@RequestParam Long idProducto,
                                          @RequestParam Long idCliente,
                                          @RequestParam String formaPago, @RequestParam String tokenCodigo) {
        log.info("/api/v1/venta");
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("venta", ventaService.crearVenta(idProducto,idCliente,formaPago,tokenCodigo));
            response.put(ConstantesMensajes.MENSAJE, "Venta creada con exito");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (NegocioException | IOException e) {
            log.error("Problemas al registrar la venta");
            response.put(ConstantesMensajes.MENSAJE, "Error al crear la venta");
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Override
    @GetMapping("/filtro")
    public ResponseEntity<Map<String, Object>> filtrarVentas(@ModelAttribute FiltroVentaDTO filtroDTO){
        log.info("/api/v1/venta/filtro");
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("ventas", ventaService.filtrarVentas(filtroDTO));
            response.put(ConstantesMensajes.MENSAJE,"Ventas de acuerdo al filtro obtenidas con exito ");
            return ResponseEntity.ok(response);
        }catch (NegocioException e) {
            log.error("No se ha podido realizar el filtro de ventas");
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getVentaById(@PathVariable("id") Long id){
        log.info("/api/v1/venta/{id}");
        Map<String, Object> response = new HashMap<>();
        try{
            VentaDTO venta= ventaService.findById(id);
            response.put("venta", venta);
            response.put(ConstantesMensajes.MENSAJE,"Venta encontrada con exito");
            return ResponseEntity.ok(response);

        }catch (NegocioException e) {
            log.error("No se ha podido encotrar la venta");
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @Override
    @GetMapping("/documento")
    public ResponseEntity<Map<String, Object>> exportar(
            @ModelAttribute FiltroVentaDTO filtroDTO,
            @RequestParam String nombreArchivo,
            @RequestParam String formato) {

        log.info("/api/v1/venta/documento");
        Map<String, Object> response = new HashMap<>();

        try {
            List<VentaDTO> ventasFiltradas = ventaService.filtrarVentas(filtroDTO);

            if ("pdf".equalsIgnoreCase(formato)) {
                exportService.exportarAPdf(ventasFiltradas, nombreArchivo + ".pdf", filtroDTO);
                response.put(ConstantesMensajes.MENSAJE, "Ventas exportadas a PDF con éxito.");
            } else if ("excel".equalsIgnoreCase(formato)) {
                exportService.exportarAExcel(ventasFiltradas, nombreArchivo + ".xlsx", filtroDTO);
                response.put(ConstantesMensajes.MENSAJE, "Ventas exportadas a Excel con éxito.");
            } else if ("ambos".equalsIgnoreCase(formato)) {
                exportService.exportarAPdf(ventasFiltradas, nombreArchivo + ".pdf", filtroDTO);
                exportService.exportarAExcel(ventasFiltradas, nombreArchivo + ".xlsx", filtroDTO);
                response.put(ConstantesMensajes.MENSAJE, "Ventas exportadas a PDF y Excel con éxito.");
            } else {
                response.put(ConstantesMensajes.ERROR, "Formato inválido. Debe ser 'pdf', 'excel' o 'ambos'.");
            }
            response.put("ventasExportadas", ventasFiltradas);
            return ResponseEntity.ok(response);

        } catch (NegocioException e) {
            log.error("No se ha podido realizar el filtro de ventas", e);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


}
