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
import ar.edu.unju.fi.poo.tp8poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.tp8poo.util.enumerated.EstadoCliente;
import ar.edu.unju.fi.poo.tp8poo.util.GestorDeImagenesUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
    public ClienteService(ClienteRepository clienteRepository,
                          ClienteMapper clienteMapper,
                          GestorDeImagenesUtil gestorDeImagenesUtil,
                          CuponService cuponService) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.gestorDeImagenesUtil = gestorDeImagenesUtil;
        this.cuponService=cuponService;
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
		ClienteEstandar clienteEstandar = (ClienteEstandar) clienteRepository.findById(id).orElseThrow(() -> {
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


    /**
     * Asigna o actualiza un cupon para un ClienteEstandar, este verifica antes si esta vencido o si aun tiene validez su
     * anterior cupon
      * @param idCliente ID del cliente a actualizar el cupon
     * @param cuponDTO nuevo cupon
     * @return retorna true si Se ha asignado el nuevo cupon o false si todavia tiene un cupon valido
     * @throws NegocioException lanza la exception si llega un formato de fecha invalido
     */
    public boolean asignarCupon(Long idCliente, CuponDTO cuponDTO) {
        try{
            ClienteEstandar clienteEstandar = (ClienteEstandar) findClienteEntityById(idCliente);
            Cupon cuponActual = clienteEstandar.getCupon();
            if (cuponActual == null || clienteEstandar.cuponVencido()) {
                cuponService.validarNuevoCupon(LocalDate.parse(cuponDTO.getFechaExpiracion()),cuponDTO.getPorcentajeDescuento());
                Cupon nuevoCupon = cuponService.crearCupon(cuponDTO);
                clienteEstandar.setCupon(nuevoCupon);
                clienteRepository.save(clienteEstandar);
                log.info("Cupón con ID {} asignado al cliente estándar con ID {}", nuevoCupon.getId(), idCliente);
                return true;
            } else {
                log.info("El cliente ya tiene un cupón asignado o el cupón actual aún es válido");
                return false;
            }
        }catch (DateTimeParseException e) {
            log.error("Formato fecha invalido");
            throw new NegocioException("Formato de fecha inválido. Debe ser YYYY-MM-DD.");
        }

    }
    /*FIN DE SECCION ESTANDAR*/

    /**
     * Valida que el porcentaje no sea nulo y que deba estar entre 0 y 100
     * @param porcentajeDescuento porcentaje de descuento para premiun
     */
    private void validarPorcentajeDescuento(Double porcentajeDescuento){
        log.debug("Validando porcentaje de descuento para Cliente premiun");
        try{
            if(porcentajeDescuento<0 || porcentajeDescuento>100){
                log.error("El porcentaje de descuento esta fuera de rango");
                throw new NegocioException("El porcentaje de descuento debe ser entre 0 y 100");
            }
        }catch (NullPointerException e){
            log.error("El porcentaje de descuento no puede ser null");
            throw new NegocioException("El porcentaje de descuento NO puede ser null");
        }
        log.info("Procentaje de descuento validado correctamente");

    }
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
        validarPorcentajeDescuento(clientePremium.getPorcentajeDescuento());
        clientePremium.setId(null);
        clienteRepository.save(clientePremium);
        log.info("Cliente: {} Agregado con extio", newClientePremium.getNombre());
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
        ClientePremium clienteExistente = (ClientePremium) findClienteEntityById(id);
        validarEmailParaEdicion(clienteExistente, dto);
        validarCelularParaEdicion(clienteExistente, dto);
        validarPorcentajeDescuento(dto.getPorcentajeDescuento());
        asignarDatosPersonales(clienteExistente,dto);
        clienteExistente.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        clienteExistente.setUpdated(LocalDateTime.now());
        clienteRepository.save(clienteExistente);
        log.info("Datos del cliente modifado con éxito");
        return clienteMapper.toClientePremiunDTO(clienteExistente);
    }

    /**
     * Obtiene un cliente Premium por su ID y lo convierte en un DTO.
     *
     * @param id El ID del cliente Premium que se desea obtener.
     * @return El ClientePremiumDTO correspondiente al cliente encontrado.
     * @throws NegocioException si no se encuentra el cliente.
     */
    public ClientePremiumDTO getClientePremium(Long id){
    	log.info("Buscando cliente con id: {}", id);
		ClientePremium clientePremium = (ClientePremium) findClienteEntityById(id);
		log.info("Cliente encontrado con éxito: {}", clientePremium.getNombre());
		return clienteMapper.toClientePremiunDTO(clientePremium);
	}
    
    
    /*FIN DE SECCION PREMIUM*/

    /**
     * Elimina lógicamente un cliente (cambia su estado a INACTIVO).
     *
     * @param clienteId El ID del cliente que se desea eliminar lógicamente.
     * @return true si la eliminación fue exitosa, false si no se encontró el cliente.
     * @throws NegocioException si no se encuentra el cliente con el ID proporcionado.
     */
    public boolean eliminarLogicamente(Long clienteId) {
    	log.info("Iniciando eliminacion del cliente con id: {}",clienteId);
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            cliente.setEstado(EstadoCliente.INACTIVO);
            clienteRepository.save(cliente);
            log.info("Cliente con id {} eliminado con exito", clienteId);
            return true;
        }
        log.error("No se encontro cliente con id: {}", clienteId);
        return false;
    }

    /**
     * Busca un cliente por su ID y lo convierte en un DTO.
     *
     * @param id El ID del cliente que se desea buscar.
     * @return El ClienteDTO correspondiente al cliente encontrado.
     * @throws NegocioException si no se encuentra el cliente.
     */
    public ClienteDTO buscarPorID(Long id) {
        Cliente cliente = findClienteEntityById(id);

        return clienteMapper.toClienteDTO(cliente);
    }


    /**
     * Obtiene todos los clientes registrados y los convierte en una lista de DTOs.
     *
     * @return Una lista de ClienteDTOs con todos los clientes.
     * @throws NegocioException si no se encuentran clientes registrados.
     */
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
     * Valida si el cliente especificado está activo para realizar compras.
     *
     * @param cliente El cliente a validar.
     * @throws NegocioException si el cliente no está activo para realizar compras.
     */
    public void validarClienteActivo(Cliente cliente){
        log.info("Validando si el cliente con ID {} está activo",cliente.getId());
        if (!cliente.getEstado().equals(EstadoCliente.ACTIVO)) {
            log.warn("El cliente con ID {} no está activo para hacer una compra", cliente.getId());
            throw new NegocioException("El cliente no esta activo para hacer una compra");
        }
    }

    /**
     * Sube una imagen para un cliente y actualiza su URL en la base de datos.
     *
     * @param idCliente El ID del cliente al que se le asociará la imagen.
     * @param imagen El archivo de la imagen a subir.
     * @return La URL de la imagen subida.
     * @throws NegocioException si no se encuentra el cliente o ocurre un error al subir la imagen.
     */
    public String subirImagenCliente(Long idCliente,MultipartFile imagen){
        Cliente cliente= findClienteEntityById(idCliente);
        String url= gestorDeImagenesUtil.subirImagen(imagen,FOLDER_NAME);
        cliente.setFoto(url);
        clienteRepository.save(cliente);
        log.info("Imagen nueva del cliente ha sido actualizada y subida al servidor");
        return url;
    }



    /**
     * Asigna los datos personales de un ClienteDTO a la entidad Cliente.
     *
     * @param cliente La entidad Cliente a la que se le asignarán los datos.
     * @param dto El ClienteDTO con los nuevos datos personales.
     */
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

    /**
     * Busca un cliente por su ID en la base de datos.
     *
     * @param id El ID del cliente que se desea buscar.
     * @return La entidad Cliente correspondiente al ID.
     * @throws NegocioException si no se encuentra el cliente con el ID proporcionado.
     */
    public Cliente findClienteEntityById(Long id) {
        log.info("Buscando cliente por ID en la entidad: {}", id);
        return clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cliente NO encontrado con ID: {}", id);
                    return new NegocioException("Cliente no encontrado");
                });
    }
}
