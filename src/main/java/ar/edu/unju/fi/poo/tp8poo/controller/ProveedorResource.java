package ar.edu.unju.fi.poo.tp8poo.controller;

import ar.edu.unju.fi.poo.tp8poo.controller.interfaces.IDocProveedorResource;
import ar.edu.unju.fi.poo.tp8poo.dto.ProveedorDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.ProveedorService;
import ar.edu.unju.fi.poo.tp8poo.util.ConstantesMensajes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/proveedor")
public class ProveedorResource implements IDocProveedorResource {

    private final ProveedorService proveedorService;

    public ProveedorResource(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @Override
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> crearProveedor(@RequestBody ProveedorDTO newProveedor) {
        log.info("Registrando nuevo proveedor: {}", newProveedor.getNombre());
        Map<String, Object> response = new HashMap<>();
        try {
            ProveedorDTO proveedorRegistrado = proveedorService.crearProveedor(newProveedor);
            response.put(ConstantesMensajes.PROVEEDOR, proveedorRegistrado);
            log.info("Proveedor {} registrado con éxito", newProveedor.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
            log.warn("Error al crear el proveedor: {}", e.getMessage());
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
  //**********************************************************************
    
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerProveedor(@PathVariable Long id) {
        log.info("Iniciando búsqueda de proveedor con id {}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            ProveedorDTO proveedorEncontrado = proveedorService.obtenerProveedorPorId(id);
            response.put(ConstantesMensajes.PROVEEDOR, proveedorEncontrado);
            log.info("Proveedor con id {} encontrado con éxito", id);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            log.warn("Error al obtener el proveedor: {}", e.getMessage());
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
  //**********************************************************************

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> modificarProveedor(
            @PathVariable Long id,
            @RequestBody ProveedorDTO proveedorDTO) {
        log.info("Iniciando proceso de modificación para el proveedor con id: {}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            ProveedorDTO proveedorEditado = proveedorService.actualizarProveedor(id, proveedorDTO);
            response.put(ConstantesMensajes.PROVEEDOR, proveedorEditado);
            log.info("Proveedor con id {} modificado con éxito", id);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            log.warn("Error al modificar el proveedor: {}", e.getMessage());
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
  //**********************************************************************

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarProveedor(@PathVariable Long id) {
        log.info("Iniciando eliminación lógica del proveedor con id {}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            proveedorService.eliminarProveedor(id);
            response.put("mensaje", "Proveedor eliminado lógicamente con éxito");
            log.info("Proveedor con id {} eliminado lógicamente", id);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            log.warn("Error al eliminar lógicamente el proveedor: {}", e.getMessage());
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
  //**********************************************************************

    @Override
    @GetMapping("/proveedores")
    public ResponseEntity<Map<String, Object>> obtenerProveedores() {
        log.info("Obteniendo listado de proveedores");
        Map<String, Object> response = new HashMap<>();
        try {
            List<ProveedorDTO> proveedoresdto = proveedorService.obtenerProveedores();
            response.put(ConstantesMensajes.PROVEEDORES, proveedoresdto);
            log.info("Listado de proveedores obtenido con éxito");
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            log.warn("Error al obtener la lista de proveedores: {}", e.getMessage());
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
