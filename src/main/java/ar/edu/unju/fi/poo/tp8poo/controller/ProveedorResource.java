package ar.edu.unju.fi.poo.tp8poo.controller;

import ar.edu.unju.fi.poo.tp8poo.dto.ProveedorDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.ProveedorService;
import ar.edu.unju.fi.poo.tp8poo.util.ConstantesMensajes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Gestión de Proveedores", description = "Operaciones relacionadas con los proveedores")
public class ProveedorResource {

    private final ProveedorService proveedorService;

    public ProveedorResource(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }
    
    String mensaje = ConstantesMensajes.MENSAJE;
    String error = ConstantesMensajes.ERROR;
    String proveedor = "proveedor";
    String proveedores = "proveedores";

    @PostMapping("")
    @Operation(
            summary = "Crear proveedor",
            description = "Registra un nuevo proveedor en el sistema",
            responses = {
                @ApiResponse(responseCode = "201", description = "Proveedor creado con éxito"),
                @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
        )
    public ResponseEntity<Map<String, Object>> crearProveedor(
    		@Parameter(description = "Proveedor DTO", required = true) 
    		@RequestBody ProveedorDTO newProveedor) {
        log.info("Registrando nuevo proveedor: {}", newProveedor.getNombre());
        Map<String, Object> response = new HashMap<>();
        try {
            ProveedorDTO proveedorRegistrado = proveedorService.crearProveedor(newProveedor);
            response.put(proveedor, proveedorRegistrado);
            log.info("Proveedor {} registrado con éxito", newProveedor.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
        	log.warn("Error al crear el proveedor: {}", e.getMessage());
            response.put(error, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener proveedor por ID",
            description = "Devuelve un proveedor basado en el ID proporcionado",
            responses = {
                @ApiResponse(responseCode = "200", description = "Proveedor obtenido con éxito"),
                @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
            }
        )
    public ResponseEntity<Map<String, Object>> obtenerProveedor(
    		@Parameter(description = "ID del proveedor", required = true)
    		@PathVariable Long id) {
        log.info("Iniciando búsqueda de proveedor con id {}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            ProveedorDTO proveedorEncontrado = proveedorService.obtenerProveedorPorId(id);
            response.put(proveedor, proveedorEncontrado);
            log.info("Proveedor con id {} encontrado con éxito", id);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
        	log.warn("Error al obtener el proveedor: {}", e.getMessage());
            response.put(error, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Modificar proveedor",
            description = "Actualiza la información de un proveedor existente en el sistema",
            responses = {
                @ApiResponse(responseCode = "200", description = "Proveedor modificado con éxito"),
                @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
            }
        )
    public ResponseEntity<Map<String, Object>> modificarProveedor(
    		@Parameter(description = "ID del proveedor", required = true) 
    		@PathVariable Long id,
            @Parameter(description = "Proveedor DTO con los datos actualizados", required = true) 
    		@RequestBody ProveedorDTO proveedorDTO) {
        log.info("Iniciando proceso de modificación para el proveedor con id: {}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            ProveedorDTO proveedorEditado = proveedorService.actualizarProveedor(id, proveedorDTO);
            response.put(proveedor, proveedorEditado);
            log.info("Proveedor con id {} modificado con éxito", id);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
        	log.warn("Error al modificar el proveedor: {}", e.getMessage());
            response.put(error, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar proveedor",
            description = "Realiza la eliminación lógica de un proveedor en el sistema",
            responses = {
                @ApiResponse(responseCode = "200", description = "Proveedor eliminado con éxito"),
                @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
            }
        )
    public ResponseEntity<Map<String, Object>> eliminarProveedor(
    		@Parameter(description = "ID del proveedor", required = true)
    		@PathVariable Long id) {
        log.info("Iniciando eliminación lógica del proveedor con id {}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            proveedorService.eliminarProveedor(id);
            response.put("mensaje", "Proveedor eliminado lógicamente con éxito");
            log.info("Proveedor con id {} eliminado lógicamente", id);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
        	log.warn("Error al eliminar logicamente el proveedor: {}", e.getMessage());
            response.put(error, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/proveedores")
    @Operation(
            summary = "Listar proveedores",
            description = "Devuelve una lista de todos los proveedores registrados en el sistema",
            responses = {
                @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida con éxito"),
                @ApiResponse(responseCode = "500", description = "Error interno al obtener la lista de proveedores")
            }
        )
    public ResponseEntity<Map<String, Object>> obtenerProveedores() {
        log.info("Obteniendo listado de proveedores");
        Map<String, Object> response = new HashMap<>();
        try {
            List<ProveedorDTO> proveedoresdto = proveedorService.obtenerProveedores();
            response.put(proveedores, proveedoresdto);
            log.info("Listado de proveedores obtenido con éxito");
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
        	log.warn("Error al obtener la lista de proveedores: {}", e.getMessage());
            response.put(error, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
