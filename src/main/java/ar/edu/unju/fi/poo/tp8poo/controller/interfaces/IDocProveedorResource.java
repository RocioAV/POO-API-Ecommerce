package ar.edu.unju.fi.poo.tp8poo.controller.interfaces;

import ar.edu.unju.fi.poo.tp8poo.dto.ProveedorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Tag(name = "Gestión de Proveedores", description = "Operaciones relacionadas con los proveedores")
public interface IDocProveedorResource {

    @Operation(
        summary = "Crear proveedor",
        description = "Registra un nuevo proveedor en el sistema",
        responses = {
            @ApiResponse(responseCode = "201", description = "Proveedor creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
        }
    )
    ResponseEntity<Map<String, Object>> crearProveedor(
        @Parameter(description = "Proveedor DTO", required = true)
        @RequestBody ProveedorDTO newProveedor
    );

    @Operation(
        summary = "Obtener proveedor por ID",
        description = "Devuelve un proveedor basado en el ID proporcionado",
        responses = {
            @ApiResponse(responseCode = "200", description = "Proveedor obtenido con éxito"),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
        }
    )
    ResponseEntity<Map<String, Object>> obtenerProveedor(
        @Parameter(description = "ID del proveedor", required = true)
        @PathVariable Long id
    );

    @Operation(
        summary = "Modificar proveedor",
        description = "Actualiza la información de un proveedor existente en el sistema",
        responses = {
            @ApiResponse(responseCode = "200", description = "Proveedor modificado con éxito"),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
        }
    )
    ResponseEntity<Map<String, Object>> modificarProveedor(
        @Parameter(description = "ID del proveedor", required = true)
        @PathVariable Long id,
        @Parameter(description = "Proveedor DTO con los datos actualizados", required = true)
        @RequestBody ProveedorDTO proveedorDTO
    );

    @Operation(
        summary = "Eliminar proveedor",
        description = "Realiza la eliminación lógica de un proveedor en el sistema",
        responses = {
            @ApiResponse(responseCode = "200", description = "Proveedor eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
        }
    )
    ResponseEntity<Map<String, Object>> eliminarProveedor(
        @Parameter(description = "ID del proveedor", required = true)
        @PathVariable Long id
    );

    @Operation(
        summary = "Listar proveedores",
        description = "Devuelve una lista de todos los proveedores registrados en el sistema",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno al obtener la lista de proveedores")
        }
    )
    ResponseEntity<Map<String, Object>> obtenerProveedores();
}

