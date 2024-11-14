package ar.edu.unju.fi.poo.tp8poo.controller;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.CuponDTO;
import ar.edu.unju.fi.poo.tp8poo.service.TokenService;
import ar.edu.unju.fi.poo.tp8poo.util.ConstantesMensajes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.ClienteService;
import org.springframework.web.multipart.MultipartFile;


@SpringBootApplication
@RestController
@Slf4j
@RequestMapping("/api/v1/cliente")
@Tag(name = "Gestion de clientes", description = "Operaciones relacionadas con los clientes")
public class ClienteResource {

	private final ClienteService clienteService;
    private final TokenService tokenService;

    public ClienteResource(ClienteService clienteService, TokenService tokenService) {
        this.clienteService = clienteService;
        this.tokenService = tokenService;
    }

    @PostMapping(value = "/estandar")
    @Operation(
            summary = "Crear cliente estándar",
            description = """
                    Para poder crear correctamente el cliente Estandar debe:
                    - Eliminar el atributo del cupon, o la fecha de expiración del cupón.
                    """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cliente estándar creado con éxito"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
    )

    public ResponseEntity<Map<String, Object>> crearClienteEstandar(@RequestBody ClienteEstandarDTO newEstandar) {
        log.info("Nuevo cliente: {}",newEstandar.getNombre());
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("estandar agregado", clienteService.agregarClienteEstandar(newEstandar));
            log.info(" {} registrado con éxito", newEstandar.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
            log.error("Problemas con {}", newEstandar.getNombre());
            response.put("mensaje:", "Error al crear el cliente estándar");
            response.put("error ocurrido", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @PostMapping(value="/premium")
    @Operation(
            summary = "Crear cliente premium",
            description = "Registra un nuevo cliente premium en el sistema",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cliente premium creado con éxito"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
    )
    public ResponseEntity<Map<String, Object>> crearClientePremium(@RequestBody ClientePremiumDTO newPremium){
        log.info("Registrando nuevo cliente estandar: {}",newPremium.getNombre());
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("premium", clienteService.agregarClientePremium(newPremium));
            log.info("Cliente {} registrado con éxito", newPremium.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
            log.error("Problemas al registrar cliente {}", newPremium.getNombre());
            response.put("mensaje 1: ", "Error al crear el cliente premium");
            response.put("error 1: ", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener cliente por ID",
            description = "Busca un cliente en el sistema usando su ID",
            parameters = @Parameter(name = "id", description = "ID del cliente a buscar", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
            }
    )

    public ResponseEntity<Map<String, Object>> obtenerCliente(@PathVariable Long id){
        log.info("Iniciando búsqueda de cliente con id {}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            log.debug("Procesando búsqueda de cliente con id {}", id);
            ClienteDTO clienteEncontrado=clienteService.buscarPorID(id);
            log.info("encuentro exitoso");
            response.put("cliente",clienteEncontrado);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            response.put("Mensaje", "Error al encontrar cliente");
            response.put("error 8: ", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/estandar/{id}")
    @Operation(
            summary = "Actualizar cliente estándar",
            description = "Modifica los datos de un cliente estándar existente",
            parameters = @Parameter(name = "id", description = "ID del cliente estándar a actualizar", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente estándar actualizado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            }
    )
    public ResponseEntity<Map<String, Object>> modificarClienteEstandar(@PathVariable Long id, @RequestBody ClienteEstandarDTO estandarDTO){
        Map<String, Object> response = new HashMap<>();
        log.info("Iniciando proceso de modificacion de datos para el cliente estandar con id: {}", id);
        try {
            ClienteEstandarDTO clienteEstandarEditado = clienteService.editarClienteEstandar(id, estandarDTO);
            log.info("Modificacion exitosa para {}", clienteEstandarEditado.getNombre());
            response.put("estandar modificado: ", clienteEstandarEditado);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            log.error("Proceso interrumpido");
            response.put("mensaje 2: ", "Error al obtener el cliente estándar");
            response.put("error 2: ", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PutMapping("/premium/{id}")
    @Operation(
            summary = "Actualizar cliente premium",
            description = "Modifica los datos de un cliente premium existente",
            parameters = @Parameter(name = "id", description = "ID del cliente premium a actualizar", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente premium actualizado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            }
    )
    public ResponseEntity<Map<String, Object>> modificarClientePremium(@PathVariable Long id, @RequestBody ClientePremiumDTO premiumDTO){
        Map<String, Object> response = new HashMap<>();
        log.info("Iniciando proceso de modificacion de datos para el cliente premium con id: {}", id);
        try {
            ClientePremiumDTO clientePremiumEditado = clienteService.editarClientePremium(id, premiumDTO);
            response.put("premium modificado: ", clientePremiumEditado);
            log.info("Proceso de modificacion completado para el cliente {}", id);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            log.error("Proceso de modificacion interrumpido para el cliente {}", id);
            response.put("mensaje 3: ", "Error al obtener el cliente premiun");
            response.put("error 3: ", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/clientes/{id}")
    @Operation(
            summary = "Eliminar cliente lógicamente",
            description = "Elimina un cliente de manera lógica usando su ID",
            parameters = @Parameter(name = "id", description = "ID del cliente a eliminar", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente eliminado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            }
    )
    public ResponseEntity<Map<String, Object>> eliminarCliente(@PathVariable Long id){
        log.info("Eliminar lógicamente cliente de id {}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            log.debug("Buscando cliente con id {}", id);
            boolean clienteEliminado = clienteService.eliminarLogicamente(id);
            if (clienteEliminado) {
                log.debug("Eliminacion completa");
                response.put("mensaje 4: ", "Cliente eliminado lógicamente con éxito");
                return ResponseEntity.ok(response);
            } else {
                log.debug("Proceso completado");
                response.put("mensaje 5: ", "Cliente no encontrado con el ID especificado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (NegocioException e) {
            log.error("Problema al eliminar cliente con id {}", id);
            response.put("mensaje 6: ", "Error al eliminar el cliente");
            response.put("error 4: ", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/clientes")
    @Operation(
            summary = "Obtener todos los clientes",
            description = "Recupera una lista de todos los clientes registrados en el sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida con éxito"),
                    @ApiResponse(responseCode = "404", description = "No se encontraron clientes")
            }
    )
    public ResponseEntity<Map<String, Object>> obtenerClientes(){
        log.info("Obteniendo listado de los clientes");
        Map<String, Object> response = new HashMap<>();
        try{
            List<ClienteDTO> clientes = clienteService.obtenerClientes();
            log.debug("Obtencion de lista exitosa");
            response.put("clientes", clientes);
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            log.warn("Error al listar");
            response.put("mensaje 7: ", "Error al obtener clientes");
            response.put("error 5: ", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PatchMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Subir imagen para cliente ",
            description = "Por medio del id del cliente, se busca al mismo para subir su imagen",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivo subido con éxito"),
                    @ApiResponse(responseCode = "404", description = "No se encontro el cliente")
            }
    )
    public ResponseEntity<Map<String, Object>> uploadFoto(@PathVariable Long id, @RequestParam("file") final MultipartFile file){
        log.info("Subida de imagen de cliente Estandar");
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("url",clienteService.subirImagenCliente(id,file));
            response.put(" mensaje ","Imagen actualizado del cliente");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NegocioException e) {
            log.error("No se encontró el cliente con id :{} ",id);
            response.put("error encontrado", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PatchMapping(value = "/estandar/{idCliente}/cupon", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Asignar cupón a cliente estándar",
            description = "Asigna un nuevo cupón a un cliente estándar si no tiene uno o si el actual ha expirado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cupón asignado con éxito al cliente estándar"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado o error de negocio")
            }
    )
    public ResponseEntity<Map<String, Object>> asignarCuponAClienteEstandar(@PathVariable Long idCliente, @RequestBody CuponDTO cupon) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean cuponAsignado = clienteService.asignarCupon(idCliente, cupon);
            if (cuponAsignado) {
                response.put("mensaje", "Cupón asignado con éxito al cliente estándar.");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("mensaje", "El cliente ya tiene un cupón válido. No es necesario asignar uno nuevo.");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

        } catch (NegocioException e) {
            log.error("Error en la asignación del cupón al cliente estándar: {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PostMapping("/{id}/token")
    @Operation(
            summary = "Generar token para cliente",
            description = "Genera un token único asociado al cliente con un valor aleatorio y lo registra en el sistema.",
            parameters = {
                    @Parameter(name = "id", description = "ID del cliente a generar el nuevo token (Long)", required = true, example = "1"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token generado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            }
    )
    public ResponseEntity<Map<String,Object>> generarTokenCliente(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try{
            ClienteDTO cliente = clienteService.buscarPorID(id);
            if (cliente!=null){
                response.put(ConstantesMensajes.MENSAJE, "Token generado para el cliente {}");
                response.put("token", tokenService.generarTokenParaCliente(id));
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (NegocioException e){
            log.error("Error al generar el token");
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }

}
