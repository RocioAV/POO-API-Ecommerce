package ar.edu.unju.fi.poo.tp8poo.service;


import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Cliente;
import ar.edu.unju.fi.poo.tp8poo.entity.ClienteEstandar;
import ar.edu.unju.fi.poo.tp8poo.entity.ClientePremium;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;
import ar.edu.unju.fi.poo.tp8poo.exceptions.*;
import ar.edu.unju.fi.poo.tp8poo.mapper.ClienteMapper;
import ar.edu.unju.fi.poo.tp8poo.mapper.CuponMapper;
import ar.edu.unju.fi.poo.tp8poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Service


public class ClienteService {
	@Autowired
	ClienteRepository clienteRepository;

    @Autowired
    ClienteMapper clienteMapper;

    
    /*SECCION DE CLIENTE ESTANDAR*/
	 // Agregar un nuevo ClienteEstandar
    public ClienteEstandarDTO agregarClienteEstandar(ClienteEstandarDTO newClienteEstandar) {

        ClienteEstandar clienteEstandar = clienteMapper.toEstandarEntity(newClienteEstandar);

        validarEmail(clienteEstandar.getEmail());
        validarCelular(clienteEstandar.getCelular());
        clienteRepository.save(clienteEstandar);
        return clienteMapper.toEstandarDTO(clienteEstandar);
    }

    private void validarEmail(String email) {
        if (clienteRepository.findByEmail(email) != null) {
            throw new EmailDuplicadoException("El cliente con dicho correo ya existe");
        }
    }

    private void validarCelular(String celular) {
        if (clienteRepository.findByCelular(celular) != null) {
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
		ClienteEstandar clienteEstandar = (ClienteEstandar) clienteRepository.findById(id)
	            .orElseThrow(() -> new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id));
		return clienteMapper.toEstandarDTO(clienteEstandar);
	}
    
    //Editar Cliente Estandar
    /**
     * 
     * @param id recibe como parametro el id del Cliente tipo Estandar
     * @param dto recibe como parametro el cliente Estandar DTO
     * @return devuelve el cliente modificado pero con dto
     */
    public ClienteEstandarDTO editarClienteEstandar(Long id, ClienteEstandarDTO dto) {

        ClienteEstandar clienteExistente = (ClienteEstandar) clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id));

        validarEmail(dto.getEmail());
        validarCelular(dto.getCelular());

        clienteExistente.setNombre(dto.getNombre());
        clienteExistente.setApellido(dto.getApellido());
        clienteExistente.setEmail(dto.getEmail());
        clienteExistente.setCelular(dto.getCelular());
        clienteExistente.setEstado(EstadoCliente.valueOf(dto.getEstado()));

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

        return clienteMapper.toEstandarDTO(clienteExistente);
    }
    
    //Eliminar Cliente Estandar
    public void eliminarClienteEstandar(Long id) {
        ClienteEstandar clienteEstandar=(ClienteEstandar) clienteRepository.findById(id)
	            .orElseThrow(() -> new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id));
        clienteRepository.deleteById(clienteEstandar.getId());
    }
    /*FIN DE SECCION ESTANDAR*/
    
    /*SECCION DE CLIENTE PREMIUM*/
    public ClientePremiumDTO agregarClientePremium(ClientePremiumDTO newClientePremium) {
        ClientePremium clientePremium = clienteMapper.toPremiumEntityDTO(newClientePremium);

        validarEmail(clientePremium.getEmail());
        validarCelular(clientePremium.getCelular());

        clienteRepository.save(clientePremium);
        return clienteMapper.toPremiumDTO(clientePremium);
    }
    
    public ClientePremiumDTO editarClientePremium(Long id, ClientePremiumDTO dto) {
        ClientePremium clienteExistente = (ClientePremium) clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id));

        validarEmail(dto.getEmail());
        validarCelular(dto.getCelular());

        clienteExistente.setNombre(dto.getNombre());
        clienteExistente.setApellido(dto.getApellido());
        clienteExistente.setEmail(dto.getEmail());
        clienteExistente.setCelular(dto.getCelular());
        clienteExistente.setEstado(EstadoCliente.valueOf(dto.getEstado()));
        clienteExistente.setPorcentajeDescuento(dto.getPorcentajeDescuento());

        clienteExistente.setUpdated(LocalDateTime.now());


        clienteRepository.save(clienteExistente);

        return clienteMapper.toPremiumDTO(clienteExistente);
    }
    
    public void eliminarClientePremium(Long id) {
        ClientePremium clientePremium=(ClientePremium) clienteRepository.findById(id)
	            .orElseThrow(() -> new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id));
        clienteRepository.deleteById(clientePremium.getId());
    }
    
    public ClientePremiumDTO getClientePremium(Long id){
		ClientePremium clientePremium = (ClientePremium) clienteRepository.findById(id)
	            .orElseThrow(() -> new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id));
		return clienteMapper.toPremiumDTO(clientePremium);
	}
    
    
    /*FIN DE SECCION PREMIUM*/

    public boolean eliminarLogicamente(Long clienteId) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            cliente.setEstado(EstadoCliente.INACTIVO);
            clienteRepository.save(cliente); // Actualiza el estado del cliente
            return true;
        }
        return false; // Retorna false si el cliente no se encuentra
    }

    public ClienteEstandarDTO buscarPorID(Long id){
        ClienteEstandar clienteEstandar = (ClienteEstandar) clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id));
        return clienteMapper.toEstandarDTO(clienteEstandar);
    }
    
    
    // Metodo para obtener todos los clientes y convertirlos a DTOs
    public List<Cliente> mostrarClientes(){
    	return clienteRepository.findAll();
    }
}
