package ar.edu.unju.fi.poo.tp8poo.service;


import ar.edu.unju.fi.poo.tp8poo.dto.ClienteDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Cliente;
import ar.edu.unju.fi.poo.tp8poo.entity.ClienteEstandar;
import ar.edu.unju.fi.poo.tp8poo.entity.ClientePremium;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;
import ar.edu.unju.fi.poo.tp8poo.exceptions.*;
import ar.edu.unju.fi.poo.tp8poo.mapper.ClienteMapper;
import ar.edu.unju.fi.poo.tp8poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@Component
public class ClienteService {
	@Autowired
	ClienteRepository clienteRepository;

    @Autowired
    ClienteMapper clienteMapper;

    
    /*SECCION DE CLIENTE ESTANDAR*/
	 // Agregar un nuevo ClienteEstandar
    public ClienteEstandarDTO agregarClienteEstandar(ClienteEstandarDTO newClienteEstandar) {
    	log.info("Agregando nuevo cliente: {}",newClienteEstandar.getNombre());
        ClienteEstandar clienteEstandar = clienteMapper.toClienteEstandarEntity(newClienteEstandar);

        validarEmail(clienteEstandar.getEmail());
        validarCelular(clienteEstandar.getCelular());
        clienteRepository.save(clienteEstandar);
        log.info("Cliente agregado con exito: {}",newClienteEstandar.getNombre());
        return clienteMapper.toClienteEstandarDTO(clienteEstandar);


    }

    private void validarEmail(String email) {
        if (clienteRepository.findByEmail(email) != null) {
        	log.error("Error al registrar cliente: El correo {} ya está registrado", email);
            throw new EmailDuplicadoException("El cliente con dicho correo ya existe");
        }
    }

    private void validarCelular(String celular) {
        if (clienteRepository.findByCelular(celular) != null) {
        	log.error("Error al registrar cliente: El número {} ya está registrado", celular);
            throw new CelularDuplicadoException("El cliente con dicho número de celular ya existe");
        }
    }
    
    // Obtener Cliente Estandar por id
    /**
     * 
     * @param id recibe como parametro el id del Cliente tipo Estandar
     * @return se devuelve el cliente Estandar pero convertido a DTO
     */
    public ClienteEstandarDTO getClienteEstandar(Long id){
    	log.info("Buscando cliente con id: {}", id);
		ClienteEstandar clienteEstandar = (ClienteEstandar) clienteRepository.findById(id)
	            .orElseThrow(() -> {
	            		log.error("Error al encontrar el cliente: {}", id);
	            		return new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id);
	            		});
		log.info("Cliente encontrado con éxito: {}", id);
		return clienteMapper.toClienteEstandarDTO(clienteEstandar);
	}
    
    //Editar Cliente Estandar
    /**
     * 
     * @param id recibe como parametro el id del Cliente tipo Estandar
     * @param dto recibe como parametro el cliente Estandar DTO
     * @return devuelve el cliente modificado pero con dto
     */
    public ClienteEstandarDTO editarClienteEstandar(Long id, ClienteEstandarDTO dto) {
    	log.info("Editando los datos del cliente: {}",dto.getNombre());

    	ClienteEstandar clienteExistente = (ClienteEstandar) clienteRepository.findById(id)
	            .orElseThrow(() -> {
	            		log.error("Error al encontrar el cliente: {}", id);
	            		return new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id);
	            		});
        validarEmail(dto.getEmail());
        validarCelular(dto.getCelular());

        clienteExistente.setNombre(dto.getNombre());
        log.debug("Cambiando nuevo nombre: {}", dto.getNombre());
        clienteExistente.setApellido(dto.getApellido());
        log.debug("Cambiando a nuevo apellido: {}", dto.getApellido());
        clienteExistente.setEmail(dto.getEmail());
        log.debug("Cambiando a nuevo email: {}", dto.getEmail());
        clienteExistente.setCelular(dto.getCelular());
        log.debug("Cambiando a nuevo celular {}", dto.getCelular());
        clienteExistente.setEstado(EstadoCliente.valueOf(dto.getEstado()));
        log.debug("Cambiando a nuevo email: {}", dto.getEstado());
        if (dto.getCupon() != null) {
            Cupon cupon = new Cupon();
            cupon.setId(dto.getCupon().getId());
            cupon.setPorcentajeDescuento(dto.getCupon().getPorcentajeDescuento());
            cupon.setFechaExpiracion(dto.getCupon().getFechaExpiracion());
            clienteExistente.setCupon(cupon);
        }

        clienteExistente.setUpdated(LocalDateTime.now());

        // Guardar el cliente actualizado
        clienteRepository.save(clienteExistente);
        log.info("Datos del cliente modificado con éxito");
        return clienteMapper.toClienteEstandarDTO(clienteExistente);

    }
    
    //Eliminar Cliente Estandar
    public void eliminarClienteEstandar(Long id) {

    	ClienteEstandar clienteEstandar = (ClienteEstandar) clienteRepository.findById(id)
	            .orElseThrow(() -> {
	            		log.error("Error al encontrar el cliente: {}", id);
	            		return new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id);
	            		});

        clienteRepository.deleteById(clienteEstandar.getId());
    }
    /*FIN DE SECCION ESTANDAR*/
    
    /*SECCION DE CLIENTE PREMIUM*/
    public ClientePremiumDTO agregarClientePremium(ClientePremiumDTO newClientePremium) {

    	log.info("Agregando nuevo cliente: {}",newClientePremium.getNombre());
        ClientePremium clientePremium = clienteMapper.toClientePremiunEntity(newClientePremium);

        validarEmail(clientePremium.getEmail());
        validarCelular(clientePremium.getCelular());
        log.info("Cliente: {} Agregado con extio", newClientePremium.getNombre());
        clienteRepository.save(clientePremium);
        return clienteMapper.toClientePremiunDTO(clientePremium);
    }
    
    public ClientePremiumDTO editarClientePremium(Long id, ClientePremiumDTO dto) {
    	log.info("Iniciando modificacion del cliente: {}", dto.getNombre());
        ClientePremium clienteExistente = (ClientePremium) clienteRepository.findById(id)
        		.orElseThrow(() -> {
            		log.error("Error al encontrar el cliente: {}", id);
            		return new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id);
            		});

        validarEmail(dto.getEmail());
        validarCelular(dto.getCelular());

        clienteExistente.setNombre(dto.getNombre());
        log.debug("Cambiando nuevo nombre: {}", dto.getNombre());
        clienteExistente.setApellido(dto.getApellido());
        log.debug("Cambiando a nuevo apellido: {}", dto.getApellido());
        clienteExistente.setEmail(dto.getEmail());
        log.debug("Cambiando a nuevo email: {}", dto.getEmail());
        clienteExistente.setCelular(dto.getCelular());
        log.debug("Cambiando a nuevo celular {}", dto.getCelular());
        clienteExistente.setEstado(EstadoCliente.valueOf(dto.getEstado()));
        log.debug("Cambiando a nuevo email: {}", dto.getEstado());
        clienteExistente.setPorcentajeDescuento(dto.getPorcentajeDescuento());

        clienteExistente.setUpdated(LocalDateTime.now());


        clienteRepository.save(clienteExistente);
        log.info("Datos del cliente modifado con éxito");
        return clienteMapper.toClientePremiunDTO(clienteExistente);
    }
    
    public void eliminarClientePremium(Long id) {
        ClientePremium clientePremium=(ClientePremium) clienteRepository.findById(id)
	            .orElseThrow(() -> new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id));
        clienteRepository.deleteById(clientePremium.getId());
    }
    
    public ClientePremiumDTO getClientePremium(Long id){
    	log.info("Buscando cliente con id: {}", id);
		ClientePremium clientePremium = (ClientePremium) clienteRepository.findById(id)
				.orElseThrow(() -> {
            		log.error("Error al encontrar el cliente: {}", id);
            		return new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id);
            		});
		log.info("Cliente encontrado con éxito: {}", clientePremium.getNombre());
		return clienteMapper.toClientePremiunDTO(clientePremium);
	}
    
    
    /*FIN DE SECCION PREMIUM*/

    public boolean eliminarLogicamente(Long clienteId) {
    	log.info("Iniciando eliminacion del cliente con id: {}",clienteId);
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            cliente.setEstado(EstadoCliente.INACTIVO);
            clienteRepository.save(cliente); // Actualiza el estado del cliente
            log.info("Cliente con id {} eliminado con exito", clienteId);
            return true;
        }
        log.error("No se encontro cliente con id: {}", clienteId);
        return false; // Retorna false si el cliente no se encuentra
    }


    public ClienteDTO buscarPorID(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al encontrar el cliente: {}", id);
                    return new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id);
                });

        return clienteMapper.toClienteDTO(cliente);
    }
    
    
    // Metodo para obtener todos los clientes y convertirlos a DTOs
    public List<Cliente> mostrarClientes(){
    	return clienteRepository.findAll();
    }
}
