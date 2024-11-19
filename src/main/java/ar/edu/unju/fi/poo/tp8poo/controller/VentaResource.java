package ar.edu.unju.fi.poo.tp8poo.controller;

import ar.edu.unju.fi.poo.tp8poo.controller.interfaces.IDocVentaResource;
import ar.edu.unju.fi.poo.tp8poo.dto.FiltroVentaDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.VentaDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
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

    public VentaResource(VentaService ventaService) {
        this.ventaService = ventaService;
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
        }catch (NegocioException | IOException e) {
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


}
