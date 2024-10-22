package ar.edu.unju.fi.poo.tp8poo.service;


import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Cliente;
import ar.edu.unju.fi.poo.tp8poo.entity.ClienteEstandar;
import ar.edu.unju.fi.poo.tp8poo.entity.ClientePremium;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ClienteExistenteException;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ClienteInexixtenteExcepcion;
import ar.edu.unju.fi.poo.tp8poo.exceptions.PorcentajeDescuentoException;
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

	/*SECCION DE CONVERSIONES*/
	/**
     * Conversión de objeto a Objeto DTO
     *
     * @param clienteEstandar se recibe a la Entidad original
     * @return la entidad como un objeto DTO.
     */

	private ClienteEstandarDTO toEstandarDTO(ClienteEstandar clienteEstandar) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		ClienteEstandarDTO clienteEstandarDTO = new ClienteEstandarDTO();
		clienteEstandarDTO.setNombre(clienteEstandar.getNombre());
		clienteEstandarDTO.setApellido(clienteEstandar.getApellido());
		clienteEstandarDTO.setCreated(clienteEstandar.getCreated().format(formatter));
		clienteEstandarDTO.setCelular(clienteEstandar.getCelular());
		clienteEstandarDTO.setFoto(clienteEstandar.getFoto());
		clienteEstandarDTO.setEmail(clienteEstandar.getEmail());
		if(clienteEstandar.getCupon().getPorcentajeDescuento()>99 || clienteEstandar.getCupon().getPorcentajeDescuento()<0) {
			throw new PorcentajeDescuentoException("Porcentaje de descuento invalido");
		}else {
			clienteEstandarDTO.setPorcentajeDescuento(clienteEstandar.getCupon().getPorcentajeDescuento());
		}
		if(clienteEstandar.getUpdated()==null) {
			clienteEstandarDTO.setUpdated(null);
		}else {
			clienteEstandarDTO.setUpdated(clienteEstandar.getUpdated().format(formatter));
		}
		clienteEstandarDTO.setId(clienteEstandar.getId());
		clienteEstandarDTO.setCupon(clienteEstandar.getCupon().getId());
		clienteEstandarDTO.setEstado(clienteEstandar.getEstado().name());//Para convertir de un enum a String
		return clienteEstandarDTO;
	}
	
	
	/**
     * Conversión de DTO a Objeto en este caso de cliente Estandar
     *
     * @param clienteEstandarDTO se recibe el objeto DTO
     * @return la entidad como una Entidad.
     * 
     */
	private ClienteEstandar toEstandarEntity(ClienteEstandarDTO clienteEstandarDTO) {
	    ClienteEstandar clienteEstandar = new ClienteEstandar();

	    clienteEstandar.setId(clienteEstandarDTO.getId()); // Opcional si es un nuevo cliente
	    clienteEstandar.setNombre(clienteEstandarDTO.getNombre());
	    clienteEstandar.setApellido(clienteEstandarDTO.getApellido());
	    clienteEstandar.setCelular(clienteEstandarDTO.getCelular());
	    clienteEstandar.setFoto(clienteEstandarDTO.getFoto());
	    clienteEstandar.setEmail(clienteEstandarDTO.getEmail());

	    // Convertir String de estado a Enum
	    clienteEstandar.setEstado(EstadoCliente.valueOf(clienteEstandarDTO.getEstado()));

	    // Manejar el cupón (evitar NPE)
	    if (clienteEstandarDTO.getCupon() != null) {
	        Cupon cupon = new Cupon();
	        cupon.setId(clienteEstandarDTO.getCupon());
	        cupon.setPorcentajeDescuento(clienteEstandarDTO.getPorcentajeDescuento());
	        clienteEstandar.setCupon(cupon);
	    }

	    // Convertir String a LocalDateTime usando DateTimeFormatter
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	    if (clienteEstandarDTO.getCreated() != null) {
	        clienteEstandar.setCreated(LocalDateTime.parse(clienteEstandarDTO.getCreated(), formatter));
	    }
	    if (clienteEstandarDTO.getUpdated() != null) {
	        clienteEstandar.setUpdated(LocalDateTime.parse(clienteEstandarDTO.getUpdated(), formatter));
	    }

	    return clienteEstandar;
	}

    /*Convertir de Entity a DTO (Premium)*/
    private ClientePremiumDTO toPremiumDTO(ClientePremium clientePremium) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        ClientePremiumDTO dtoPremium = new ClientePremiumDTO();
        dtoPremium.setId(clientePremium.getId());
        dtoPremium.setNombre(clientePremium.getNombre());
        dtoPremium.setApellido(clientePremium.getApellido());
        dtoPremium.setEmail(clientePremium.getEmail());
        dtoPremium.setCelular(clientePremium.getCelular());
        dtoPremium.setPorcentajeDescuento(clientePremium.getPorcentajeDescuento());
        dtoPremium.setCreated(clientePremium.getCreated().format(formatter));
        dtoPremium.setEstado(clientePremium.getEstado().name());
        return dtoPremium;
    }

    /*Convertir de DTO a Entity(Premium)*/

    private ClientePremium toPremiumEntityDTO(ClientePremiumDTO dto) {
        ClientePremium clientePremium = new ClientePremium();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        clientePremium.setId(Long.valueOf(dto.getId()));
        clientePremium.setNombre(dto.getNombre());
        clientePremium.setApellido(dto.getApellido());
        clientePremium.setEmail(dto.getEmail());
        clientePremium.setCelular(dto.getCelular());
        clientePremium.setEstado(EstadoCliente.valueOf(dto.getEstado()));
        clientePremium.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        clientePremium.setCreated(LocalDateTime.parse(dto.getCreated(), formatter));
        return clientePremium;
    }
    

	
    
    /*FIN DE SECCION DE CONVERSION*/
    
    /*SECCION DE CLIENTE ESTANDAR*/
	 // Agregar un nuevo ClienteEstandar
    public void agregarClienteEstandar(ClienteEstandarDTO newClienteEstandar) {
        ClienteEstandar clienteEstandar = toEstandarEntity(newClienteEstandar);

        validarEmail(clienteEstandar.getEmail());
        validarCelular(clienteEstandar.getCelular());

        clienteRepository.save(clienteEstandar);
    }

    private void validarEmail(String email) {
        if (clienteRepository.findByEmail(email) != null) {
            throw new ClienteExistenteException("El cliente con dicho correo ya existe");
        }
    }

    private void validarCelular(String celular) {
        if (clienteRepository.findByCelular(celular) != null) {
            throw new ClienteExistenteException("El cliente con dicho número de celular ya existe");
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
		return toEstandarDTO(clienteEstandar);
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

        // Actualizar los campos
        clienteExistente.setNombre(dto.getNombre());
        clienteExistente.setApellido(dto.getApellido());
        clienteExistente.setEmail(dto.getEmail());
        clienteExistente.setCelular(dto.getCelular());
        clienteExistente.setEstado(EstadoCliente.valueOf(dto.getEstado()));

        if (dto.getCupon() != null) {
            Cupon cupon = new Cupon();
            cupon.setId(dto.getCupon());
            cupon.setPorcentajeDescuento(dto.getPorcentajeDescuento());
            clienteExistente.setCupon(cupon);
        }

        clienteExistente.setUpdated(LocalDateTime.now());

        // Guardar el cliente actualizado
        clienteRepository.save(clienteExistente);

        return toEstandarDTO(clienteExistente);
    }
    
    //Eliminar Cliente Estandar
    public void eliminarClienteEstandar(Long id) {
        ClienteEstandar clienteEstandar=(ClienteEstandar) clienteRepository.findById(id)
	            .orElseThrow(() -> new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id));
        clienteRepository.deleteById(clienteEstandar.getId());
    }
    /*FIN DE SECCION ESTANDAR*/
    
    /*SECCION DE CLIENTE PREMIUM*/
    public void agregarClientePremium(ClientePremiumDTO newClientePremium) {
        ClientePremium clientePremium = toPremiumEntityDTO(newClientePremium);
        validarEmail(newClientePremium.getEmail());
        validarCelular(newClientePremium.getCelular());
        clienteRepository.save(clientePremium);
    }
    
    public ClientePremiumDTO editarClientePremium(Long id, ClientePremiumDTO dto) {
        ClientePremium clienteExistente = (ClientePremium) clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id));

        validarEmail(dto.getEmail());
        validarCelular(dto.getCelular());

        // Actualizar los campos
        clienteExistente.setNombre(dto.getNombre());
        clienteExistente.setApellido(dto.getApellido());
        clienteExistente.setEmail(dto.getEmail());
        clienteExistente.setCelular(dto.getCelular());
        clienteExistente.setEstado(EstadoCliente.valueOf(dto.getEstado()));
        clienteExistente.setPorcentajeDescuento(dto.getPorcentajeDescuento());

        clienteExistente.setUpdated(LocalDateTime.now());

        // Guardar el cliente actualizado
        clienteRepository.save(clienteExistente);

        return toPremiumDTO(clienteExistente);
    }
    
    public void eliminarClientePremium(Long id) {
        ClientePremium clientePremium=(ClientePremium) clienteRepository.findById(id)
	            .orElseThrow(() -> new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id));
        clienteRepository.deleteById(clientePremium.getId());
    }
    
    public ClientePremiumDTO getClientePremium(Long id){
		ClientePremium clientePremium = (ClientePremium) clienteRepository.findById(id)
	            .orElseThrow(() -> new ClienteInexixtenteExcepcion("Cliente no encontrado con ID: " + id));
		return toPremiumDTO(clientePremium);
	}
    
    
    /*FIN DE SECCION PREMIUM*/

    
    
    
    // Metodo para obtener todos los clientes y convertirlos a DTOs
    public List<Cliente> mostrarClientes(){
    	return clienteRepository.findAll();
    }
}
