package ar.edu.unju.fi.poo.tp8poo.controller;

import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/producto")
@Slf4j
@Tag(name = "Gestion de Producto", description = "Operaciones CRUD entre otras relacionadas a Producto")
public class ProductoResource {
    private final ProductoService productoService;


    public ProductoResource(ProductoService productoService) {this.productoService = productoService;
    }

    @GetMapping("/list")
    @Operation(
            summary = "Obtener todos los productos",
            description = "Devuelve una lista de todos los productos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Productos obtenidos con éxito"),
                    @ApiResponse(responseCode = "204", description = "No hay productos disponibles"),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al obtener el producto")
            }
    )
    public ResponseEntity<?> getAllProductos() {
        log.info("/api/v1/producto/list");
        Map<String, Object> response = new HashMap<>();
        try {
            List<ProductoDTO> productos = productoService.findAll();
            if (productos.isEmpty()) {
                log.info("No se encontraron productos");
                return ResponseEntity.noContent().build();
            }
            response.put("productos", productos);
            response.put("mensaje", "Productos obtenidos con éxito");
            return ResponseEntity.ok(response);
        }catch (DataAccessException e) {
            log.error("Error de acceso a la base de datos al obtener el producto por ID", e);
            response.put("mensaje", "Error de base de datos al obtener el producto");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }


    @GetMapping("/get/{id}")
    @Operation(
            summary = "Obtener producto por ID",
            description = "Devuelve un producto específico por su ID",
            parameters = @Parameter(name = "id",description = "ID del producto", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto obtenido con éxito"),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al obtener el producto")
            }
    )
    public ResponseEntity<?> getProductoById(@PathVariable Long id) {
        log.info("/api/v1/producto/get/{id}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            ProductoDTO productoDTO = productoService.findById(id);
            if (productoDTO == null) {
                log.warn("Producto con ID [{}] no encontrado", id);
                return ResponseEntity.notFound().build();
            }
            response.put("producto", productoDTO);
            response.put("mensaje", "Producto obtenido con éxito");
            return ResponseEntity.ok(response);
        }catch (EntityNotFoundException e){
            log.error("No se encontro el producto con ID [{}]", id);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }catch (DataAccessException e) {
            log.error("Error de acceso a la base de datos al obtener el producto por ID", e);
            response.put("mensaje", "Error de base de datos al obtener el producto");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }



    @PostMapping("/create")
    @Operation(
            summary = "Crea un producto",
            description = "Crea un producto nuevo con datos proporcionados en el body",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Producto creado con éxito"),
                    @ApiResponse(responseCode = "400", description = "Error en los datos enviados para crear el producto"),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al crear el producto")
            }
    )
    public ResponseEntity<?> createProducto(
            @Parameter(description = "Producto DTO", required = true)
            @RequestBody ProductoDTO productoDTO) {
        log.info("/api/v1/producto/create");
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("producto", productoService.createProducto(productoDTO));
            response.put("mensaje", "Producto creado con éxito");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
            log.warn("Error al crear el producto: {}", e.getMessage());
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (DataAccessException e) {
            log.error("Error de acceso a la base de datos al crear el producto", e);
            response.put("mensaje", "Error de base de datos al crear el producto");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }



    @PutMapping("/update/{id}")
    @Operation(
            summary = "Actualizar un producto",
            description = "Actualiza un producto existente con el ID y datos proporcionados",
            parameters = @Parameter(name = "id",description = "ID del producto", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto actualizado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al actualizar el producto")
            }
    )
    public ResponseEntity<?> updateProducto(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        log.info("/api/v1/producto/update/{id}", id);
        Map<String, Object> response = new HashMap<>();
        try{
            ProductoDTO updatedProducto = productoService.editProducto(id, productoDTO);
            if (updatedProducto == null) {
                log.warn("Producto con ID [{}] no encontrado para actualizar", id);
                return ResponseEntity.notFound().build();
            }
            response.put("producto", updatedProducto);
            response.put("mensaje", "Producto actualizado con éxito");
            return ResponseEntity.ok(response);
        }catch (EntityNotFoundException e){
            log.error("No se encontro el producto con ID [{}] para editar", id);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }catch (DataAccessException e) {
            log.error("Error de acceso a la base de datos al actualizar el producto", e);
            response.put("mensaje", "Error de base de datos al actualizar el producto");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }


    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Eliminar un producto",
            description = "Realiza una eliminación lógica del producto especificado por ID",
            parameters = @Parameter(name = "id",description = "ID del producto", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto eliminado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al eliminar el producto", content = @Content)
            }
    )
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        log.info("/api/v1/producto/delete/{id}", id);
        Map<String, Object> response = new HashMap<>();
        try{
            ProductoDTO producto = productoService.deleteProductoLogico(id);
            if (producto == null) {
                log.warn("Producto con ID [{}] no encontrado para eliminar", id);
                return ResponseEntity.notFound().build();
            }
            response.put("producto", producto);
            response.put("mensaje", "Producto eliminado con éxito");
            return ResponseEntity.ok(response);
        }catch (EntityNotFoundException e){
            log.error("No se encontro el producto con ID [{}] para eliminar", id);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }catch (DataAccessException e) {
            log.error("Error de acceso a la base de datos al eliminar el producto", e);
            response.put("mensaje", "Error de base de datos al eliminar el producto");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PostMapping(value= "/upload/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Subir imagen de un producto",
            description = "Carga una imagen para un producto específico por ID",
            parameters = @Parameter(name = "id",description = "ID del producto", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Imagen actualizada con éxito"),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor al actualizar la imagen")
            }
    )
    public ResponseEntity<?> uploadFotoProducto (@PathVariable Long id, @RequestParam("file") final MultipartFile file) {
        log.info("/api/v1/producto/upload/{id}/file", id);
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("producto",productoService.subirImagenProducto(id,file));
            response.put("mensaje", "Imagen actualizada con exito:");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (EntityNotFoundException e){
            log.error("No se encontro el producto con ID [{}] para eliminar", id);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }catch (Exception e){
            log.error("Error interno en el servidor");
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



}
