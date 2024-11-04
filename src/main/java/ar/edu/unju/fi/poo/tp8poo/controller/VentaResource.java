package ar.edu.unju.fi.poo.tp8poo.controller;

import ar.edu.unju.fi.poo.tp8poo.dto.FiltroVentaDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.VentaDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.VentaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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
public class VentaResource {

    private final VentaService ventaService;

    public VentaResource(VentaService ventaService) {
        this.ventaService = ventaService;
    }


    @GetMapping("/list")
    public ResponseEntity<?> getAllVentas(){
        log.info("/api/v1/venta/list");
        Map<String, Object> response = new HashMap<>();
        try {
            List<VentaDTO> ventas = ventaService.findAll();
            if (ventas.isEmpty()) {
                log.info("No se encontraron ventas");
                response.put("message", "No se encontraron ventas");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("ventas", ventas);
            response.put("mensaje", "Ventas obtenidas con éxito");
            return ResponseEntity.ok(response);
        }catch (DataAccessException e) {
            log.error("Error de acceso a la base de datos al obtener el producto por ID", e);
            response.put("mensaje", "Error de base de datos al obtener el producto");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> agregarVenta(@RequestParam Long idProducto,
                                          @RequestParam Long idCliente,
                                          @RequestParam String formaPago) {
        log.info("/api/v1/venta/create");
        Map<String, Object> response = new HashMap<>();
        if (idProducto == null || idCliente == null || formaPago == null) {
            log.error("Faltan parámetros obligatorios");
            response.put("mensaje", "Todos los parámetros son obligatorios y no pueden ser nulos.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try{
            response.put("venta", ventaService.crearVenta(idProducto,idCliente,formaPago));
            response.put("mensaje", "Venta creada con exito");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (NegocioException e) {
            log.error("Problemas al registrar la venta");
            response.put("mensaje", "Error al crear la venta");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IOException e) {
            log.error("Problemas al registrar la venta");
            response.put("mensaje", "Error al convertir moneda");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            log.error("Proceso de creacion interrumpido");
            response.put("mensaje", "Error interno del servidor");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filtrarVentas(@ModelAttribute FiltroVentaDTO filtroDTO){
        log.info("/api/v1/venta/filter");
        Map<String, Object> response = new HashMap<>();
        try{
            List<VentaDTO> ventasFiltradas= ventaService.filtrarVentas(filtroDTO);
            if(ventasFiltradas.isEmpty()){
                log.info("No se encontraron ventas que concuerden con el filtro");
                return ResponseEntity.noContent().build();
            }
            response.put("ventas", ventasFiltradas);
            response.put("mensaje","Ventas de acuerdo al filtro obtenidas con exito ");
            return ResponseEntity.ok(response);

        }catch (NegocioException e) {
            response.put("mensaje", "Error al aplicar filtros");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al consultar el hsitoria de ventas");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getVentaById(@PathVariable("id") Long id){
        log.info("/api/v1/venta/get/{id}");
        Map<String, Object> response = new HashMap<>();
        try{
            VentaDTO venta= ventaService.findById(id);
            if(venta==null){
                log.info("No se encontraron venta que con el id "+id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("venta", venta);
            response.put("mensaje","Venta encontrada con exito");
            return ResponseEntity.ok(response);

        }catch (NegocioException e) {
            response.put("mensaje", "Error al buscar la venta");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al buscar la venta");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
