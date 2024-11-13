package ar.edu.unju.fi.poo.tp8poo.service;


import ar.edu.unju.fi.poo.tp8poo.dto.ClienteDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;

import ar.edu.unju.fi.poo.tp8poo.dto.CuponDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Cliente;
import ar.edu.unju.fi.poo.tp8poo.entity.ClienteEstandar;
import ar.edu.unju.fi.poo.tp8poo.entity.ClientePremium;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.mapper.ClienteMapper;
import ar.edu.unju.fi.poo.tp8poo.mapper.CuponMapper;
import ar.edu.unju.fi.poo.tp8poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.tp8poo.repository.CuponRepository;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import ar.edu.unju.fi.poo.tp8poo.util.GestorDeImagenesUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@Component
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final GestorDeImagenesUtil gestorDeImagenesUtil;
    private final CuponService cuponService;
    private final CuponRepository cuponRepository;
    private final CuponMapper cuponMapper;
    public ClienteService(ClienteRepository clienteRepository,
                          ClienteMapper clienteMapper,
                          CuponMapper cuponMapper,
                          GestorDeImagenesUtil gestorDeImagenesUtil,
                          CuponService cuponService,
                          CuponRepository cuponRepository) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.gestorDeImagenesUtil = gestorDeImagenesUtil;
        this.cuponService=cuponService;
        this.cuponMapper=cuponMapper;
        this.cuponRepository=cuponRepository;
    }
   	
   	private static final String FOLDER_NAME = "avatars";
    private static final String DEFAULT_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/tp8poo2024.firebasestorage.app/o/avatars%2Fdefault.webp?alt=media&token=8a77e93a-cea1-4a1a-bfb0-36c0e8bc433d";



    /**
     * Valida si el nuevo clienteEditadoDTO cambio de email para volver a validarlo
     * @param cliente cliente de la base de datos
     * @param clienteEditadoDTO cliente editado que llega del body
     */
    private void validarEmailParaEdicion(Cliente cliente, ClienteDTO clienteEditadoDTO){
        if (!cliente.getEmail().equals(clienteEditadoDTO.getEmail())) {
            validarEmail(clienteEditadoDTO.getEmail());
        }
    }

    /**
     * Valida si el nuevo clienteEditadoDTO cambio de celular para volver a validarlo
     * @param cliente cliente de la base de datos
     * @param clienteEditadoDTO cliente que llega del body con cambios
     */
    private void validarCelularParaEdicion(Cliente cliente, ClienteDTO clienteEditadoDTO){
        if (!cliente.getCelular().equals(clienteEditadoDTO.getCelular())) {
            validarCelular(clienteEditadoDTO.getCelular());
        }
    }
    /**
     * Valida que el email no esté registrado en el sistema.
     *
     * @param email Email a validar.
     * @throws NegocioException si el email ya está registrado.
     */
    private void validarEmail(String email) {
        if (clienteRepository.findByEmail(email) != null) {
            log.error("Error al registrar cliente: El correo {} ya está registrado", email);
            throw new NegocioException("El cliente con dicho correo ya existe");
        }
    }

    /**
     * Valida que el número de celular no esté registrado en el sistema.
     *
     * @param celular Número de celular a validar.
     * @throws NegocioException si el celular ya está registrado.
     */
    private void validarCelular(String celular) {
        if (clienteRepository.findByCelular(celular) != null) {
            log.error("Error al registrar cliente: El número {} ya está registrado", celular);
            throw new NegocioException("El cliente con dicho número de celular ya existe");
        }
    }
    
    /*SECCION DE CLIENTE ESTANDAR*/
    /**
     * Agrega un nuevo cliente de tipo estándar al sistema.
     * Valida la unicidad del email y celular antes de la creación.
     *
     * @param newClienteEstandar Datos del cliente estándar a agregar.
     * @return ClienteEstandarDTO con los datos del cliente registrado.
     */
    public  ClienteEstandarDTO agregarClienteEstandar(ClienteEstandarDTO newClienteEstandar) {
    	log.info("Agregando  cliente: {}",newClienteEstandar.getNombre());
        newClienteEstandar.setFoto(DEFAULT_IMAGE_URL);
        ClienteEstandar clienteEstandar = clienteMapper.toClienteEstandarEntity(newClienteEstandar);
        validarEmail(clienteEstandar.getEmail());
        validarCelular(clienteEstandar.getCelular());
        clienteEstandar.setId(null);
        clienteEstandar.setCupon(null);
		clienteRepository.save(clienteEstandar);
        log.info("Cliente agregado con exito: {}",newClienteEstandar.getNombre());
        return clienteMapper.toClienteEstandarDTO(clienteEstandar);

    }


    /**
     * Obtiene un cliente estándar por su ID.
     *
     * @param id ID del cliente estándar.
     * @return ClienteEstandarDTO con los datos del cliente.
     * @throws NegocioException si no se encuentra el cliente.
     */
    public ClienteEstandarDTO getClienteEstandar(Long id){
    	log.info("Buscando cliente : {}", id);
		ClienteEstandar clienteEstandar = (ClienteEstandar) clienteRepository.findById(id)
	            .orElseThrow(() -> {
	            		log.error("ERROR al encontrar el cliente: {}", id);
	            		return new NegocioException("Cliente no encontrado con");
	            		});
		log.info(" Cliente encontrado con éxito: {}", id);
		return clienteMapper.toClienteEstandarDTO(clienteEstandar);
	}

    /**
     * Edita los datos de un cliente estándar existente.
     *
     * @param id  ID del cliente a editar.
     * @param dto Datos actualizados del cliente estándar.
     * @return ClienteEstandarDTO con los datos actualizados.
     * @throws NegocioException si no se encuentra el cliente o hay conflicto con el email/celular.
     */
    public ClienteEstandarDTO editarClienteEstandar(Long id, ClienteEstandarDTO dto) {
    	log.info("Editando los datos del cliente: {}",dto.getNombre());
    	ClienteEstandar clienteExistente = (ClienteEstandar) clienteRepository.findById(id)
	            .orElseThrow(() -> {
	            		log.error(" Error al encontrar el cliente: {}", id);
	            		return new NegocioException("Cliente estandar no encontrado no registrado con id"+ id);
	            		});
        validarEmailParaEdicion(clienteExistente,dto);
        validarCelularParaEdicion(clienteExistente,dto);
        asignarDatosPersonales(clienteExistente,dto);
        clienteExistente.setUpdated(LocalDateTime.now());
        clienteRepository.save(clienteExistente);
        log.info("Datos del cliente modificado con éxito");
        return clienteMapper.toClienteEstandarDTO(clienteExistente);
    }


    public boolean asignarCupon(Long idCliente, CuponDTO cuponDTO) {
        ClienteEstandar clienteEstandar = (ClienteEstandar) clienteRepository.findById(idCliente)
                .orElseThrow(() -> {
                    log.error("Error al encontrar el cliente estándar con ID: {}", idCliente);
                    return new NegocioException("Cliente estándar no registrado con ID " + idCliente);
                });
        Cupon cuponActual = clienteEstandar.getCupon();
        if (cuponActual == null || cuponService.isExpirado(cuponActual.getFechaExpiracion())) {
            LocalDate fechaExpiracionNueva = LocalDate.parse(cuponDTO.getFechaExpiracion());
            if (!fechaExpiracionNueva.isAfter(LocalDate.now())) {
                log.warn("La fecha de expiración del nuevo cupón debe ser posterior a la fecha actual.");
                throw new NegocioException("La fecha de expiración del cupón debe ser posterior a la fecha actual.");
            }
            if (cuponDTO.getPorcentajeDescuento() <= 0 || cuponDTO.getPorcentajeDescuento() >= 100) {
                log.warn("El porcentaje de descuento debe estar entre 0 y 100.");
                throw new NegocioException("El porcentaje de descuento debe estar entre 0 y 100.");
            }
            Cupon nuevoCupon = cuponMapper.toCuponEntity(cuponDTO);
            nuevoCupon = cuponRepository.save(nuevoCupon);
            clienteEstandar.setCupon(nuevoCupon);
            clienteRepository.save(clienteEstandar);
            log.info("Cupón con ID {} asignado al cliente estándar con ID {}", nuevoCupon.getId(), idCliente);
            return true;
        } else {
            log.info("El cliente ya tiene un cupón asignado o el cupón actual aún es válido");
            return false;
        }
    }
    /*FIN DE SECCION ESTANDAR*/
    
    /*SECCION DE CLIENTE PREMIUM*/
    /**
     * Agrega un nuevo cliente de tipo premium al sistema.
     * Valida la unicidad del email y celular antes de la creación.
     *
     * @param newClientePremium Datos del cliente premium a agregar.
     * @return ClientePremiumDTO con los datos del cliente registrado.
     */
    public ClientePremiumDTO agregarClientePremium(ClientePremiumDTO newClientePremium) {

    	log.info("Agregando nuevo cliente: {}",newClientePremium.getNombre());
		newClientePremium.setFoto(DEFAULT_IMAGE_URL);
        ClientePremium clientePremium = clienteMapper.toClientePremiunEntity(newClientePremium);

        validarEmail(clientePremium.getEmail());
        validarCelular(clientePremium.getCelular());
        clientePremium.setId(null);
        log.info("Cliente: {} Agregado con extio", newClientePremium.getNombre());
        clienteRepository.save(clientePremium);
        return clienteMapper.toClientePremiunDTO(clientePremium);
    }


    /**
     * Edita los datos de un cliente premium existente.
     *
     * @param id  ID del cliente a editar.
     * @param dto Datos actualizados del cliente premium.
     * @return ClientePremiumDTO con los datos actualizados.
     */
    public ClientePremiumDTO editarClientePremium(Long id, ClientePremiumDTO dto) {
    	log.info("Iniciando modificacion del cliente: {}", dto.getNombre());
        ClientePremium clienteExistente = (ClientePremium) clienteRepository.findById(id)
        		.orElseThrow(() -> {
            		log.error("Error al encontrar el cliente con id: {}", id);
            		return new NegocioException("Cliente premium no registrado con ID: " + id);
            		});
        validarEmailParaEdicion(clienteExistente, dto);
        validarCelularParaEdicion(clienteExistente, dto);

        asignarDatosPersonales(clienteExistente,dto);

        clienteExistente.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        clienteExistente.setUpdated(LocalDateTime.now());
        clienteRepository.save(clienteExistente);
        log.info("Datos del cliente modifado con éxito");
        return clienteMapper.toClientePremiunDTO(clienteExistente);
    }

    
    public ClientePremiumDTO getClientePremium(Long id){
    	log.info("Buscando cliente con id: {}", id);
		ClientePremium clientePremium = (ClientePremium) clienteRepository.findById(id)
				.orElseThrow(() -> {
            		log.error("Error al encontrar el cliente premium: {}", id);
            		return new NegocioException("Cliente no encontrado con ID: " + id);
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
                    return new NegocioException("Cliente no encontrado con ID: " + id);
                });

        return clienteMapper.toClienteDTO(cliente);
    }
    
    
    // Metodo para obtener todos los clientes y convertirlos a DTOs
    public List<ClienteDTO> obtenerClientes() {
        log.info("Obteniendo clientes");
        List<Cliente> clientes = clienteRepository.findAll();

        if(!clientes.isEmpty()){
            return clienteMapper.toClienteDtoList(clientes);
        }else{
            log.error("Proceso interrumpido");
            throw new NegocioException("No hay ningún cliente registrado");
        }
    }

    /**
     * Valida si el cliente con el ID especificado está activo para realizar compras.
     *
     * @param id ID del cliente a validar.
     * @throws NegocioException si el cliente no está activo.
     */
    public void validarClienteActivo(Long id){
        log.info("Validando si el cliente con ID {} está activo", id);
        ClienteDTO cliente = buscarPorID(id);
        if (!cliente.getEstado().equals(EstadoCliente.ACTIVO.name())) {
            log.warn("El cliente con ID {} no está activo para hacer una compra", id);
            throw new NegocioException("El cliente no esta activo para hacer una compra");
        }
    }


    public ClienteEstandarDTO subirImagenClienteEstandar(Long clienteId, MultipartFile imagen)  {
        ClienteEstandarDTO clienteEstandar=getClienteEstandar(clienteId);
        String url= gestorDeImagenesUtil.subirImagen(imagen,FOLDER_NAME);
        clienteEstandar.setFoto(url);
        return editarClienteEstandar(clienteEstandar.getId(),clienteEstandar);
    }

    public ClientePremiumDTO subirImagenClientePremium(Long clienteId, MultipartFile imagen)  {
        ClientePremiumDTO clientePremium=getClientePremium(clienteId);
        String url= gestorDeImagenesUtil.subirImagen(imagen,FOLDER_NAME);
        clientePremium.setFoto(url);
        return editarClientePremium(clientePremium.getId(),clientePremium);
    }

    private void asignarDatosPersonales(Cliente cliente, ClienteDTO dto) {
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setEmail(dto.getEmail());
        cliente.setCelular(dto.getCelular());
        cliente.setEstado(EstadoCliente.valueOf(dto.getEstado()));
        if(dto.getFoto()!=null && !dto.getFoto().equals("string")){
            cliente.setFoto(dto.getFoto());
        }
        log.debug("Datos personales actualizados: Nombre={}, Apellido={}, Email={}, Celular={}, Estado={}, foto={}",
                dto.getNombre(), dto.getApellido(), dto.getEmail(), dto.getCelular(),dto.getEstado(),dto.getFoto());
    }
}
