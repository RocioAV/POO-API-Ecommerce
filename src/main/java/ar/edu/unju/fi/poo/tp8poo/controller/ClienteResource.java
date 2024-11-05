package ar.edu.unju.fi.poo.tp8poo.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@SpringBootApplication
@RestController
@Slf4j
@RequestMapping("/api/v1/cliente")
@Tag(name = "Gestion de clientes", description = "Operaciones relacionadas con los clientes")
public class ClienteResource {

	private final ClienteService clienteService;

    public ClienteResource(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping(value = "/estandar")
    @Operation(
            summary = "Crear cliente estándar",
            description = "Registra un nuevo cliente estándar en el sistema",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cliente estándar creado con éxito"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            }
    )

    public ResponseEntity<?> crearClienteEstandar(@RequestBody ClienteEstandarDTO newEstandar) {
        log.info("Registrando nuevo cliente estandar: {}",newEstandar.getNombre());
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("estandar", clienteService.agregarClienteEstandar(newEstandar));
            log.info("Cliente {} registrado con éxito", newEstandar.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
            log.error("Problemas al registrar cliente {}", newEstandar.getNombre());
            response.put("mensaje", "Error al crear el cliente estándar");
            response.put("error", e.getMessage());
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
    public ResponseEntity<?> crearClientePremium(@RequestBody ClientePremiumDTO newPremium){
        log.info("Registrando nuevo cliente estandar: {}",newPremium.getNombre());
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("premium", clienteService.agregarClientePremium(newPremium));
            log.info("Cliente {} registrado con éxito", newPremium.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
            log.error("Problemas al registrar cliente {}", newPremium.getNombre());
            response.put("mensaje", "Error al crear el cliente premium");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/get/{id}")
    @Operation(
            summary = "Obtener cliente por ID",
            description = "Busca un cliente en el sistema usando su ID",
            parameters = @Parameter(name = "id", description = "ID del cliente a buscar", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
            }
    )

    public ResponseEntity<?> obtenerCliente(@PathVariable Long id){
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
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update/estandar/{id}")
    @Operation(
            summary = "Actualizar cliente estándar",
            description = "Modifica los datos de un cliente estándar existente",
            parameters = @Parameter(name = "id", description = "ID del cliente estándar a actualizar", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente estándar actualizado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            }
    )
    public ResponseEntity<?> modificarClienteEstandar(@PathVariable Long id, @RequestBody ClienteEstandarDTO estandarDTO){
        Map<String, Object> response = new HashMap<>();
        log.info("Iniciando proceso de modificacion de datos para el cliente estandar con id: {}", id);
        try {
            ClienteEstandarDTO clienteEstandarEditado = clienteService.editarClienteEstandar(id, estandarDTO);
            log.info("Proceso de modificacion completado para el cliente {}", id);
            response.put("estandar", clienteEstandarEditado);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            log.error("Proceso de modificacion interrumpido para el cliente {}", id);
            response.put("mensaje", "Error al obtener el cliente estándar");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PutMapping("/update/premium/{id}")
    @Operation(
            summary = "Actualizar cliente premium",
            description = "Modifica los datos de un cliente premium existente",
            parameters = @Parameter(name = "id", description = "ID del cliente premium a actualizar", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente premium actualizado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            }
    )
    public ResponseEntity<?> modificarClientePremium(@PathVariable Long id, @RequestBody ClientePremiumDTO premiumDTO){
        Map<String, Object> response = new HashMap<>();
        log.info("Iniciando proceso de modificacion de datos para el cliente premium con id: {}", id);
        try {
            ClientePremiumDTO clientePremiumEditado = clienteService.editarClientePremium(id, premiumDTO);
            response.put("estandar", clientePremiumEditado);
            log.info("Proceso de modificacion completado para el cliente {}", id);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            log.error("Proceso de modificacion interrumpido para el cliente {}", id);
            response.put("mensaje", "Error al obtener el cliente estándar");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }



    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Eliminar cliente lógicamente",
            description = "Elimina un cliente de manera lógica usando su ID",
            parameters = @Parameter(name = "id", description = "ID del cliente a eliminar", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente eliminado con éxito"),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            }
    )
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id){
        log.info("Eliminar lógicamente cliente de id {}", id);
        Map<String, Object> response = new HashMap<>();
        try {
            log.debug("Buscando cliente con id {}", id);
            boolean clienteEliminado = clienteService.eliminarLogicamente(id);
            if (clienteEliminado) {
                log.debug("Proceso completado");
                response.put("mensaje", "Cliente eliminado lógicamente con éxito");
                return ResponseEntity.ok(response);
            } else {
                log.debug("Proceso completado");
                response.put("mensaje", "Cliente no encontrado con el ID especificado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (NegocioException e) {
            log.error("Problema al eliminar cliente con id {}", id);
            response.put("mensaje", "Error al eliminar el cliente");
            response.put("error", e.getMessage());
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
    public ResponseEntity<?> obtenerClientes(){
        log.info("Obteniendo listado de los clientes");
        Map<String, Object> response = new HashMap<>();
        try{
            List<ClienteDTO> clientes = clienteService.obtenerClientes();
            log.debug("Obtencion de lista exitosa");
            response.put("clientes", clientes);
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            log.warn("Error al listar");
            response.put("mensaje", "Error al obtener clientes");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PatchMapping(value = "/upload/estandar/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Subir imagen para cliente Estandar",
            description = "Por medio del id del cliente, se busca al mismo para subir su imagen",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida con éxito"),
                    @ApiResponse(responseCode = "404", description = "No se encontraron clientes")
            }
    )
    public ResponseEntity<?> uploadFotoEstandar(@PathVariable Long id, @RequestParam("file") final MultipartFile file){
        log.info("Subida de imagen");
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("estandar",clienteService.subirImagenClienteEstandar(id,file));
            response.put("mensaje","Imagen actualizado del cliente");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NegocioException e) {
            log.error("No se encontró el cliente");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PatchMapping(value = "/upload/premium/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Subir imagen para cliente Premium",
            description = "Por medio del id del cliente, se busca al mismo para subir su imagen",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida con éxito"),
                    @ApiResponse(responseCode = "404", description = "No se encontraron clientes")
            }
    )
    public ResponseEntity<?> uploadFotoPremium(@PathVariable Long id, @RequestParam("file") final MultipartFile file){
        log.info("Subida de imagen");
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("estandar",clienteService.subirImagenClientePremium(id,file));
            response.put("mensaje","Imagen actualizado del cliente");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NegocioException e) {
            log.error("No se encontró el cliente");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


}
