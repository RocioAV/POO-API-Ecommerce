package ar.edu.unju.fi.poo.tp8poo.controller.interfaces;

import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "Gestion de Producto", description = "Operaciones CRUD entre otras relacionadas a Producto")
public interface IDocProductoResource {

    @Operation(
            summary = "Obtener todos los productos",
            description = "Devuelve una lista de todos los productos en la base de datos - NO recibe datos de entrada",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Productos obtenidos con éxito" ,content = @Content),
                    @ApiResponse(responseCode = "204", description = "No hay productos disponibles",content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al obtener el producto",content = @Content)
            }
    )
    public ResponseEntity<Map<String, Object>> getAllProductos();

    //**********************************************************************

    @Operation(
            summary = "Obtener producto por ID",
            description = "Devuelve un producto específico por su ID -  RECIBE un dato por **PARAMETRO** el ID debe ser tipo **Long/number** ",
            parameters = @Parameter(name = "id",description = "ID del producto ", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto obtenido con éxito",content = @Content),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado",content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al obtener el producto",content = @Content)
            }
    )
    public ResponseEntity<Map<String, Object>> getProductoById(@PathVariable Long id);

    //**********************************************************************

    @Operation(
            summary = "Crea un producto",
            description =""" 
                    Crea un producto nuevo con datos proporcionados en el **BODY**,
                    -ELIMINAR ATRIBUTO ID
                    -ELIMINAR ATRIBUTO IMAGEN (crea con una url por default)
                          "codigo": "String",
                          "nombre": "String",
                          "descripcion": "String",
                          "precio": Double,
                          "cantidad": Integer,
                          "estado": "String", (Disponible, No Disponible)
                          "idProveedor": Long
                """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Producto creado con éxito",content = @Content),
                    @ApiResponse(responseCode = "400", description = "Error en los datos enviados para crear el producto"),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al crear el producto")
            }
    )
    public ResponseEntity<Map<String, Object>> createProducto(@Parameter(description = "Producto DTO", required = true) @RequestBody ProductoDTO productoDTO);

//**********************************************************************

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
                          "estado": "String", (Disponible, No Disponible)
                          "idProveedor": Long
                    Todos los datos vistos deben estar presente al editar, cambiar solo los que se van actualizar\s
               \s""",
            parameters = @Parameter(name = "id",description = "ID del producto", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto actualizado con éxito",content = @Content),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado",content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al actualizar el producto",content = @Content)
            }
    )
    public ResponseEntity<Map<String, Object>> updateProducto(@PathVariable Long id, @RequestBody ProductoDTO productoDTO);


    //**********************************************************************


    @Operation(
            summary = "Eliminar un producto",
            description =
                    """
                            Realiza una eliminación lógica del producto especificado por ID
                            UNICO PARAMETRO
                            - Id del prodcuto (Long/number)
                            """,
            parameters = @Parameter(name = "id",description = "ID del producto", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto eliminado con éxito",content = @Content),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado",content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error de base de datos al eliminar el producto", content = @Content)
            }
    )
    public ResponseEntity<Map<String, Object>> deleteProducto(@PathVariable Long id);

    //**********************************************************************

    @Operation(
            summary = "Subir imagen de un producto",
            description = """
                    Carga una imagen para un producto específico por ID.
                    PARAMETROS OBLIGATORIOS
                    - Id del producto (Long/number)
                    - Archivo Imagen (éste debe ser .jpg .jpeg .webp .png)
                    """,
            parameters = @Parameter(name = "id",description = "ID del producto", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Imagen actualizada con éxito",content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor al actualizar la imagen")
            }
    )
    public ResponseEntity<Map<String, Object>> uploadFotoProducto (@PathVariable Long id, @RequestParam("file") final MultipartFile file);
    //**********************************************************************

    @Operation(
            summary = "Buscar productos por nombre",
            description = """
                    Busca productos cuyo nombre contenga el parámetro proporcionado
                    PARAMETROS
                    - nombre (String)
                    """,
            parameters = @Parameter(name = "nombre", description = "Nombre del producto (String)", required = true, example = "Laptop"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Productos obtenidos con éxito", content = @Content),
                    @ApiResponse(responseCode = "204", description = "No se encontraron productos con el nombre especificado", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud de búsqueda", content = @Content)
            }
    )
    public ResponseEntity<Map<String, Object>> buscarProductoPorNombre(@RequestParam String nombre);
    //**********************************************************************

    @Operation(
            summary = "Buscar productos por descripción",
            description = """
                    Busca productos cuya descripción contenga el parámetro proporcionado
                     PARAMETROS
                    - descripcion (String)
                    """,
            parameters = @Parameter(name = "description", description = "Descripción del producto", required = true, example = "Pantalla táctil"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Productos obtenidos con éxito", content = @Content),
                    @ApiResponse(responseCode = "204", description = "No se encontraron productos con la descripción especificada", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud de búsqueda", content = @Content)
            }
    )
    public ResponseEntity<Map<String, Object>> buscarProductoPorDescripcion(@RequestParam String description);
    //**********************************************************************

    @Operation(
            summary = "Buscar producto por código",
            description = """
                    Busca un producto cuyo código coincida explicitamente con el parámetro proporcionado
                    PARAMETROS
                    - codigo (String)
                    """,
            parameters = @Parameter(name = "codigo", description = "Código del producto (String)", required = true, example = "PROD001"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Producto obtenido con éxito", content = @Content),
                    @ApiResponse(responseCode = "204", description = "No se encontró producto con el código especificado", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Error en la solicitud de búsqueda", content = @Content)
            }
    )
    public ResponseEntity<Map<String, Object>> buscarProductoPorCodigo(@RequestParam String codigo);
    //**********************************************************************
}
