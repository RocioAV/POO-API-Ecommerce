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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.*;

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
        log.info("GET /api/v1/venta/ventas");
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
        log.info("POST /api/v1/venta");
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("venta", ventaService.crearVenta(idProducto,idCliente,formaPago,tokenCodigo));
            response.put(ConstantesMensajes.MENSAJE, "Venta creada con exito");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (NegocioException e) {
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getVentaById(@PathVariable("id") Long id){
        log.info("GET /api/v1/venta/{id}");
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("venta", ventaService.findById(id));
            response.put(ConstantesMensajes.MENSAJE,"Venta encontrada con exito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e) {
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @Override
    @GetMapping("/filtro")
    public ResponseEntity<Object> exportar(
            FiltroVentaDTO filtroDTO,
            @RequestParam(required = false) String formato) {
        log.info("/api/v1/venta/filtro/documento solicitado en formato: {}", formato);
        try {
            List<VentaDTO> ventasFiltradas = ventaService.filtrarVentas(filtroDTO);
            if (ventasFiltradas.isEmpty()) {
                log.warn("No se encontraron ventas para exportar.");
                return ResponseEntity.noContent().build();
            }
            if (formato == null || formato.isBlank()) {
                log.warn("Parámetro 'formato' no proporcionado. Devolviendo lista filtrada en formato JSON.");
                return ResponseEntity.ok(ventasFiltradas);
            }
            String nombreArchivo = ("Ventas " + exportService.obtenerTitulo(filtroDTO));
            byte[] archivoBytes = exportService.exportarArchivo(ventasFiltradas, filtroDTO, nombreArchivo, formato);
            if (archivoBytes == null) {
                log.error(ConstantesMensajes.ERROR,"Formato inválido o error en la exportación.");
                return ResponseEntity.badRequest().body(Map.of(
                		ConstantesMensajes.ERROR, "El formato especificado no es válido.",
                        "estado", HttpStatus.BAD_REQUEST.value()
                ));
            }
            HttpHeaders headers = exportService.establecerEncabezados(nombreArchivo, formato);
            return ResponseEntity.ok().headers(headers).body(archivoBytes);
        } catch (NegocioException e) {
            log.error(ConstantesMensajes.ERROR,"Error en la exportación: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            		ConstantesMensajes.MENSAJE, e.getMessage(),
                    "estado", HttpStatus.BAD_REQUEST.value()
            ));
        }
    }
    
}
