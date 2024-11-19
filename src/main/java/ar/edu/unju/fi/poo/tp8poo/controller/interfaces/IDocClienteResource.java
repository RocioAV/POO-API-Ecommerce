package ar.edu.unju.fi.poo.tp8poo.controller.interfaces;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.CuponDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "Gestion de clientes", description = "Operaciones relacionadas con los clientes")
public interface IDocClienteResource {

    @Operation(
            summary = "Crear cliente estándar",
            description = """
            Para poder crear correctamente el cliente Estandar debe:
            - **Eliminar el atributo del cupon** (Se crea en null por default)
            - **Eliminar id**
            - **Eliminar foto** (se crea con una url por default)
            Campos requeridos:
            ```json
            {
              "nombre": "String",
              "apellido": "String",
              "celular": "String",
              "email": "String",
              "estado": "String" (Se crea ACTIVO por default)
            }
            ```
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cliente estándar creado con éxito"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
    )
    public ResponseEntity<Map<String, Object>> crearClienteEstandar(@RequestBody ClienteEstandarDTO newEstandar);

    /*******************************************************************************************************************/

    @Operation(
            summary = "Crear cliente premium",
            description = """
            Para poder crear correctamente el cliente Premiun debe:
            - **Eliminar id**
            - **Eliminar foto** (se crea con una url por default)
            Campos requeridos:
            ```json
            {
              "nombre": "String",
              "apellido": "String",
              "celular": "String",
              "email": "String",
              "estado": "String" (Se crea ACTIVO por default)
              "porcentajeDescuento":Double
            }
            ```
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cliente premium creado con éxito"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
    )
    public ResponseEntity<Map<String, Object>> crearClientePremium(@RequestBody ClientePremiumDTO newPremium);

    /****************************************************************************************************/

    @Operation(
            summary = "Obtener cliente por ID",
            description = "Busca un cliente en el sistema usando su ID",
            parameters = @Parameter(name = "id", description = "ID del cliente a buscar", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
            }
    )

    public ResponseEntity<Map<String, Object>> obtenerCliente(@PathVariable Long id);

    /************************************************************************************************/

    @Operation(
            summary = "Actualizar cliente estándar",
            description = """
            Modifica los datos de un cliente estándar existente.

            - **Eliminar cupon**
            - **Eliminar id**
            - **Eliminar foto** (solo se actualiza con el endpoint `api/v1/cliente/{id}/imagen`)

            Campos requeridos:
            ```json
            {
              "nombre": "String",
              "apellido": "String",
              "celular": "String",
              "email": "String",
              "estado": "ACTIVO" (ACTIVO, SUSPENDIDO, INACTIVO)
            }
            ```
            """,
            parameters = @Parameter(name = "id", description = "ID del cliente estándar a actualizar", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente estándar actualizado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            }
    )
    public ResponseEntity<Map<String, Object>> modificarClienteEstandar(@PathVariable Long id, @RequestBody ClienteEstandarDTO estandarDTO);

    /************************************************************************************************/

    @Operation(
            summary = "Actualizar cliente premium",
            description =  """
            Modifica los datos de un cliente PREMIUM existente.

            - **Eliminar id**
            - **Eliminar foto** (solo se actualiza con el endpoint `api/v1/cliente/{id}/imagen`)

            Campos requeridos:
            ```json
            {
              "nombre": "String",
              "apellido": "String",
              "celular": "String",
              "email": "String",
              "estado": "ACTIVO" (ACTIVO, SUSPENDIDO, INACTIVO),
              "porcentajeDescuento": Double
            }
            ```
            """,
            parameters = @Parameter(name = "id", description = "ID del cliente premium a actualizar", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente premium actualizado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            }
    )
    public ResponseEntity<Map<String, Object>> modificarClientePremium(@PathVariable Long id, @RequestBody ClientePremiumDTO premiumDTO);

    /************************************************************************************************/


    @Operation(
            summary = "Eliminar cliente lógicamente",
            description = "Elimina un cliente de manera lógica usando su ID (Long/number)",
            parameters = @Parameter(name = "id", description = "ID del cliente a eliminar", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente eliminado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            }
    )
    public ResponseEntity<Map<String, Object>> eliminarCliente(@PathVariable Long id);

    /************************************************************************************************/

    @Operation(
            summary = "Obtener todos los clientes",
            description = "Recupera una lista de todos los clientes registrados en el sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida con éxito"),
                    @ApiResponse(responseCode = "404", description = "No se encontraron clientes")
            }
    )
    public ResponseEntity<Map<String, Object>> obtenerClientes();

    /************************************************************************************************/

    @Operation(
            summary = "Subir imagen para cliente ",
            description ="""
            Por medio del id del cliente, se busca al mismo para subir su imagen.

            **Parámetros obligatorios:**
            - **Id del cliente** (Long/Number)
            - **Archivo Imagen** (este debe ser .jpg, .jpeg, .webp o .png)
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo subido con éxito"),
                    @ApiResponse(responseCode = "404", description = "No se encontro el cliente")
            }
    )
    public ResponseEntity<Map<String, Object>> uploadFoto(@PathVariable Long id, @RequestParam("file") final MultipartFile file);

    /************************************************************************************************/

    @Operation(
            summary = "Asignar cupón a cliente estándar",
            description = """
            Asigna un nuevo cupón a un cliente estándar si no tiene uno o si el actual ha expirado.

            **Parámetros obligatorios:**
            - **ID cliente** (Long/Number)

            **Cuerpo de la solicitud (Body):**
            (Eliminar id)
            ```json
            {
                "fechaExpiracion": "String" (formato: YYYY-MM-DD),
                "porcentajeDescuento": Double
            }
            ```
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cupón asignado con éxito al cliente estándar"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado o error de negocio")
            }
    )
    public ResponseEntity<Map<String, Object>> asignarCuponAClienteEstandar(@PathVariable Long idCliente, @RequestBody CuponDTO cupon);

    /************************************************************************************************/

    @Operation(
            summary = "Generar token para cliente",
            description = """
            Genera un token único asociado al cliente con un valor aleatorio y lo registra en el sistema.

            **Parámetros obligatorios:**
            - **ID cliente** (Long/Number)
            """,
            parameters = {
                    @Parameter(name = "id", description = "ID del cliente a generar el nuevo token (Long)", required = true, example = "1"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token generado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            }
    )
    public ResponseEntity<Map<String,Object>> generarTokenCliente(@PathVariable Long id);



}
