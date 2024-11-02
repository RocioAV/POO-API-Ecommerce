package ar.edu.unju.fi.poo.tp8poo.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.ClientePremium;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.ClienteService;

@SpringBootApplication
@RestController
@RequestMapping("/api/v1/cliente")
public class ClienteResource {
	
	private final ClienteService clienteService;

    public ClienteResource(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
    @PostMapping("/estandar")
    public ResponseEntity<?> crearClienteEstandar(@RequestBody ClienteEstandarDTO newEstandar) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("estandar", clienteService.agregarClienteEstandar(newEstandar));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
            response.put("mensaje", "Error al crear el cliente estándar");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/premium")
    public ResponseEntity<?> crearClientePremium(@RequestBody ClientePremiumDTO newPremium){
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("premium", clienteService.agregarClientePremium(newPremium));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
            response.put("mensaje", "Error al crear el cliente premium");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> obtenerCliente(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        try {
            ClienteDTO clienteEncontrado=clienteService.buscarPorID(id);
            response.put("cliente",clienteEncontrado);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            response.put("Mensaje", "Error al encontrar cliente");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update/estandar/{id}")
    public ResponseEntity<?> modificarClienteEstandar(@PathVariable Long id, @RequestBody ClienteEstandarDTO estandarDTO){
        Map<String, Object> response = new HashMap<>();
        try {
            ClienteEstandarDTO clienteEstandarEditado = clienteService.editarClienteEstandar(id, estandarDTO);
            response.put("estandar", clienteEstandarEditado);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            response.put("mensaje", "Error al obtener el cliente estándar");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("mensaje", "Error interno del servidor");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update/premium/{id}")
    public ResponseEntity<?> modificarClienteEstandar(@PathVariable Long id, @RequestBody ClientePremiumDTO premiumDTO){
        Map<String, Object> response = new HashMap<>();
        try {
            ClientePremiumDTO clientePremiumEditado = clienteService.editarClientePremium(id, premiumDTO);
            response.put("estandar", clientePremiumEditado);
            return ResponseEntity.ok(response);
        } catch (NegocioException e) {
            response.put("mensaje", "Error al obtener el cliente estándar");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("mensaje", "Error interno del servidor");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id){
        Map<String, Object> response = new HashMap<>();
        try {
            boolean clienteEliminado = clienteService.eliminarLogicamente(id);
            if (clienteEliminado) {
                response.put("mensaje", "Cliente eliminado lógicamente con éxito");
                return ResponseEntity.ok(response);
            } else {
                response.put("mensaje", "Cliente no encontrado con el ID especificado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (NegocioException e) {
            response.put("mensaje", "Error al eliminar el cliente");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/clientes")
    public ResponseEntity<?> obtenerClientes(){
        Map<String, Object> response = new HashMap<>();
        try{
            List<ClienteDTO> clientes = clienteService.obtenerClientes();
            response.put("clientes", clientes);
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            response.put("mensaje", "Error al obtener clientes");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


}
