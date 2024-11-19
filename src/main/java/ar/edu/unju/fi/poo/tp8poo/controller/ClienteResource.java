package ar.edu.unju.fi.poo.tp8poo.controller;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.unju.fi.poo.tp8poo.controller.interfaces.IDocClienteResource;
import ar.edu.unju.fi.poo.tp8poo.dto.ClienteDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.CuponDTO;
import ar.edu.unju.fi.poo.tp8poo.service.TokenService;
import ar.edu.unju.fi.poo.tp8poo.util.ConstantesMensajes;
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
public class ClienteResource implements IDocClienteResource {

	private final ClienteService clienteService;
    private final TokenService tokenService;

    public ClienteResource(ClienteService clienteService, TokenService tokenService) {
        this.clienteService = clienteService;
        this.tokenService = tokenService;
    }

    @Override
    @PostMapping(value = "/estandar")
    public ResponseEntity<Map<String, Object>> crearClienteEstandar(@RequestBody ClienteEstandarDTO newEstandar) {
        log.info("POST /api/v1/cliente/estandar");
        Map<String, Object> response = new HashMap<>();
        try {
            response.put(ConstantesMensajes.CLIENTE, clienteService.agregarClienteEstandar(newEstandar));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Override
    @PostMapping(value="/premium")
    public ResponseEntity<Map<String, Object>> crearClientePremium(@RequestBody ClientePremiumDTO newPremium){
        log.info(" POST /api/v1/cliente/premium");
        Map<String, Object> response = new HashMap<>();
        try {
            response.put(ConstantesMensajes.CLIENTE, clienteService.agregarClientePremium(newPremium));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerCliente(@PathVariable Long id){
        log.info("GET /api/v1/cliente /{}",id);
        Map<String, Object> response = new HashMap<>();
        try {
            response.put(ConstantesMensajes.CLIENTE,clienteService.buscarPorID(id));
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    @PutMapping("/estandar/{id}")
    public ResponseEntity<Map<String, Object>> modificarClienteEstandar(@PathVariable Long id, @RequestBody ClienteEstandarDTO estandarDTO){
        log.info("PUT /api/ v1/cliente/estandar/{}",id);
        Map<String, Object> response = new HashMap<>();
        log.info("Iniciando proceso de modificacion de datos para el cliente estandar con id: {}", id);
        try {
            response.put(ConstantesMensajes.CLIENTE, clienteService.editarClienteEstandar(id, estandarDTO));
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            log.error("Proceso interrumpido");
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Override
    @PutMapping("/premium/{id}")
    public ResponseEntity<Map<String, Object>> modificarClientePremium(@PathVariable Long id, @RequestBody ClientePremiumDTO premiumDTO){
        log.info("PUT  /api/v1/cliente/premium/{}",id);
        Map<String, Object> response = new HashMap<>();
        log.info("Iniciando proceso de modificacion de datos para el cliente premium con id: {}", id);
        try {
            ClientePremiumDTO clientePremiumEditado = clienteService.editarClientePremium(id, premiumDTO);
            response.put(ConstantesMensajes.CLIENTE, clientePremiumEditado);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            log.error("Proceso de modificacion interrumpido para el cliente {}", id);
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarCliente(@PathVariable Long id){
        log.info("DELETE /api/v1/cliente/{}",id);
        Map<String, Object> response = new HashMap<>();
        try {
            log.debug("Buscando cliente con id {}", id);
            boolean clienteEliminado = clienteService.eliminarLogicamente(id);
            if (clienteEliminado) {
                response.put(ConstantesMensajes.MENSAJE, "Cliente eliminado lógicamente con éxito");
                return ResponseEntity.ok(response);
            } else {
                response.put(ConstantesMensajes.MENSAJE, "Cliente no encontrado con el ID especificado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (NegocioException e) {
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    @GetMapping("/clientes")
    public ResponseEntity<Map<String, Object>> obtenerClientes(){
        log.info("GET /api/v1/cliente/clientes");
        Map<String, Object> response = new HashMap<>();
        try{
            List<ClienteDTO> clientes = clienteService.obtenerClientes();
            response.put(ConstantesMensajes.CLIENTES, clientes);
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Override
    @PatchMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadFoto(@PathVariable Long id, @RequestParam("file") final MultipartFile file){
        log.info("PATCH /api/v1/cliente/{}/imagen",id);
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("url",clienteService.subirImagenCliente(id,file));
            response.put(ConstantesMensajes.MENSAJE,"Imagen actualizado del cliente");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (NegocioException e) {
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @Override
    @PatchMapping(value = "/estandar/{idCliente}/cupon", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> asignarCuponAClienteEstandar(@PathVariable Long idCliente, @RequestBody CuponDTO cupon) {
        log.info("PATCH api/v1/cliente/estandar/{}/cupon",idCliente);
        Map<String, Object> response = new HashMap<>();
        try {
            boolean cuponAsignado = clienteService.asignarCupon(idCliente, cupon);
            if (cuponAsignado) {
                response.put(ConstantesMensajes.MENSAJE, "Cupón asignado con éxito al cliente estándar.");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put(ConstantesMensajes.MENSAJE, "El cliente ya tiene un cupón válido. No es necesario asignar uno nuevo.");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }

        } catch (NegocioException e) {
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Override
    @PostMapping("/{id}/token")
    public ResponseEntity<Map<String,Object>> generarTokenCliente(@PathVariable Long id) {
        log.info("POST api/v1/cliente/{}/token",id);
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("token", tokenService.generarTokenParaCliente(id));
            response.put(ConstantesMensajes.MENSAJE, "Token generado para el cliente {}");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (NegocioException e){
            log.error("Error al generar el token");
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }

}
