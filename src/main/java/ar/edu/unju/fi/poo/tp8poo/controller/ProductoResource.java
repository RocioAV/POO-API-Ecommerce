package ar.edu.unju.fi.poo.tp8poo.controller;

import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    private static final String MENSAJE="mensaje";
    private static final String ERROR="error";

    @GetMapping("/list")
    @Operation(
            summary = "Obtener todos los productos",
            description = "Devuelve una lista de todos los productos en la base de datos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Productos obtenidos con éxito" ,content = @Content),
                    @ApiResponse(responseCode = "204", description = "No hay productos disponibles",content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al obtener el producto",content = @Content)
            }
    )
    public ResponseEntity<?> getAllProductos() {
        log.info("/api/v1/producto/list");
        Map<String, Object> response = new HashMap<>();
        List<ProductoDTO> productos = productoService.findAll();
        if (productos.isEmpty()) {
            log.info("No se encontraron productos");
            return ResponseEntity.noContent().build();
        }
        response.put("productos", productos);
        response.put(MENSAJE, "Productos obtenidos con éxito");
        return ResponseEntity.ok(response);

    }


    @GetMapping("/get/{id}")
    @Operation(
            summary = "Obtener producto por ID",
            description = "Devuelve un producto específico por su ID",
            parameters = @Parameter(name = "id",description = "ID del producto (Long)", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto obtenido con éxito",content = @Content),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado",content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al obtener el producto",content = @Content)
            }
    )
    public ResponseEntity<?> getProductoById(@PathVariable Long id) {
        log.info("/api/v1/producto/get/{}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("producto", productoService.findById(id));
            response.put(MENSAJE, "Producto obtenido con éxito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            log.error("No se encontro el producto con ID [{}]", id);
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }



    @PostMapping("/create")
    @Operation(
            summary = "Crea un producto",
            description =""" 
                    Crea un producto nuevo con datos proporcionados en el body,
                    -ELIMINAR ATRIBUTO ID
                    -ELIMINAR ATRIBUTO IMAGEN (crea con una url por default)
                          "codigo": "String",
                          "nombre": "String",
                          "descripcion": "String",
                          "precio": Double,
                          "cantidad": Integer,
                          "estado": "String",
                          "idProveedor": Integer
                """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Producto creado con éxito",content = @Content),
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
            response.put(MENSAJE, "Producto creado con éxito");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
            log.warn("Error al crear el producto: {}", e.getMessage());
            response.put(MENSAJE, "Error al crear el producto");
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }



    @PutMapping("/update/{id}")
    @Operation(
            summary = "Actualizar un producto",
            description = """
                    Actualiza un producto existente con el ID y datos proporcionados,
                    - ELIMINAR ATRIBUTO ID
                    - ELIMINAR ATRIBUTO IMAGEN (solo se edita con el enpoint upload/{id}/foto)
                          "codigo": "String",
                          "nombre": "String",
                          "descripcion": "String",
                          "precio": Double,
                          "cantidad": Integer,
                          "estado": "String",
                          "idProveedor": Integer
                    Todos los datos vistos deben estar presente al editar, cambiar solo los que se van actualizar\s
               \s""",
            parameters = @Parameter(name = "id",description = "ID del producto", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto actualizado con éxito",content = @Content),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado",content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al actualizar el producto",content = @Content)
            }
    )
    public ResponseEntity<?> updateProducto(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        log.info("/api/v1/producto/update/{}", id);
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("producto", productoService.editProducto(id, productoDTO));
            response.put(MENSAJE, "Producto actualizado con éxito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            log.error("El proceso de editado se ha interrumpido para producto [{}]",id);
            response.put(MENSAJE, "Error al actualizar el producto");
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }


    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Eliminar un producto",
            description = "Realiza una eliminación lógica del producto especificado por ID",
            parameters = @Parameter(name = "id",description = "ID del producto (Long)", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto eliminado con éxito",content = @Content),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado",content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al eliminar el producto", content = @Content)
            }
    )
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        log.info("/api/v1/producto/delete/{}", id);
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("producto", productoService.deleteProductoLogico(id));
            response.put(MENSAJE, "Producto eliminado con éxito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            log.error("El proceso de eliminado logico se ha interrumpido para producto [{}]",id);
            response.put(MENSAJE, "Error al aliminar el producto");
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }

    @PatchMapping(value= "/upload/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Subir imagen de un producto",
            description = "Carga una imagen para un producto específico por ID",
            parameters = @Parameter(name = "id",description = "ID del producto(Long)", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Imagen actualizada con éxito",content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor al actualizar la imagen")
            }
    )
    public ResponseEntity<?> uploadFotoProducto (@PathVariable Long id, @RequestParam("file") final MultipartFile file) {
        log.info("/api/v1/producto/upload/{}/file", id);
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("producto",productoService.subirImagenProducto(id,file));
            response.put(MENSAJE, "Imagen actualizada con exito:");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (NegocioException e){
            log.error("Se ha interrumpido la actualizacion de imagen");
            response.put(MENSAJE, "Error al actualizar la imagen del producto");
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/find/nombre")
    @Operation(
            summary = "Buscar productos por nombre",
            description = "Busca productos cuyo nombre coincida con el parámetro proporcionado",
            parameters = @Parameter(name = "nombre", description = "Nombre del producto(String)", required = true, example = "Laptop"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Productos obtenidos con éxito", content = @Content),
                    @ApiResponse(responseCode = "204", description = "No se encontraron productos con el nombre especificado", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud de búsqueda", content = @Content)
            }
    )
    public ResponseEntity<?> buscarProductoPorNombre(@RequestParam String nombre) {
        log.info("/api/v1/producto/find/nombre");
        Map<String, Object> response = new HashMap<>();
        try{
            List<ProductoDTO> productos = productoService.findByNombre(nombre);
            if (productos.isEmpty()) {
                log.info("No se encontraron productos coincidentes con el nombre");
                return ResponseEntity.noContent().build();
            }
            response.put("productos", productos);
            response.put(MENSAJE, "Productos obtenidos con éxito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            log.error("Se ha interrumpido la busqueda por nombre");
            response.put(MENSAJE, "Error al buscar productos por nombre");
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/find/description")
    @Operation(
            summary = "Buscar productos por descripción",
            description = "Busca productos cuya descripción coincida con el parámetro proporcionado",
            parameters = @Parameter(name = "description", description = "Descripción del producto (String)", required = true, example = "Pantalla táctil"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Productos obtenidos con éxito", content = @Content),
                    @ApiResponse(responseCode = "204", description = "No se encontraron productos con la descripción especificada", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud de búsqueda", content = @Content)
            }
    )
    public ResponseEntity<?> buscarProductoPorDescripcion(@RequestParam String description) {
        log.info("/api/v1/producto/find/description");
        Map<String, Object> response = new HashMap<>();
        try{
            List<ProductoDTO> productos = productoService.findByDescripcion(description);
            if (productos.isEmpty()) {
                log.info("No se encontraron productos coincidentes con la descripcion");
                return ResponseEntity.noContent().build();
            }
            response.put("productos", productos);
            response.put(MENSAJE, "Productos obtenidos con éxito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            log.error("Se ha interrumpido la busqueda por descripcion");
            response.put(MENSAJE, "Error al buscar productos por descripcion");
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/find/codigo")
    @Operation(
            summary = "Buscar producto por código",
            description = "Busca un producto cuyo código coincida con el parámetro proporcionado",
            parameters = @Parameter(name = "codigo", description = "Código del producto (String)", required = true, example = "PROD001"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto obtenido con éxito", content = @Content),
                    @ApiResponse(responseCode = "204", description = "No se encontró producto con el código especificado", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud de búsqueda", content = @Content)
            }
    )
    public ResponseEntity<?> buscarProductoPorCodigo(@RequestParam String codigo) {
        log.info("/api/v1/producto/find/codigo");
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("producto", productoService.findByCodigo(codigo));
            response.put(MENSAJE, "Producto obtenidos con éxito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            log.error("Se ha interrumpido la busqueda por codigo");
            response.put(MENSAJE, "Error al buscar producto por codigo");
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }



}
