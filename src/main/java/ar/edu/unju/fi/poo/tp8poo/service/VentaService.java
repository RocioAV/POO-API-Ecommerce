package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.*;
import ar.edu.unju.fi.poo.tp8poo.entity.Venta;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.mapper.VentaMapper;
import ar.edu.unju.fi.poo.tp8poo.repository.VentaRepository;
import ar.edu.unju.fi.poo.tp8poo.util.ConversorMoneda;
import ar.edu.unju.fi.poo.tp8poo.util.FormaPago;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class VentaService {
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final VentaRepository ventaRepository;
    private final VentaMapper ventaMapper;
    private final EmailService emailService;
    private final DescuentoService descuentoService;

    public VentaService(ClienteService clienteService,
                        ProductoService productoService,
                        VentaRepository ventaRepository,
                        VentaMapper ventaMapper,
                        EmailService emailService,
                        DescuentoService descuentoService) {
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.ventaRepository = ventaRepository;
        this.ventaMapper = ventaMapper;
        this.emailService = emailService;
        this.descuentoService = descuentoService;
    }


    /**
     * Valida que el cliente y el producto estén disponibles para la venta.
     *
     * @param idCliente ID del cliente.
     * @param idProducto ID del producto.
     */
    private void validarDatosVenta(Long idCliente, Long idProducto) {
        log.info("Validando datos para la venta. Cliente ID: {}, Producto ID: {}", idCliente, idProducto);
        clienteService.validarClienteActivo(idCliente);
        productoService.validarProductoSinStock(idProducto);
        log.info("Validaciones de cliente y producto completadas para Cliente ID: {} y Producto ID: {}", idCliente, idProducto);
    }
    private String parsearFechaHoy(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
    /**
     * Prepara el objeto VentaDTO con los datos necesarios para registrar una venta.
     *
     * @param clienteDTO Cliente que realiza la compra.
     * @param productoDTO Producto a vender.
     * @param formaDePago Forma de pago elegida para la compra.
     * @return Objeto VentaDTO con los datos de la venta preparada.
     * @throws IOException Si ocurre un error al preparar los datos.
     */
    private VentaDTO prepararVentaDTO(ClienteDTO clienteDTO, ProductoDTO productoDTO, String formaDePago) throws IOException {
        VentaDTO ventaDTO = new VentaDTO();
        log.debug("Preparando venta para cliente con ID {}", clienteDTO.getId());
        ventaDTO.setFechaYHora(parsearFechaHoy());
        ventaDTO.setProducto(productoDTO);
        ventaDTO.setCliente(clienteDTO);
        validarFormaPago(formaDePago);
        ventaDTO.setFormaPago(formaDePago);
        Double precioFinalConDescuento = descuentoService.aplicarDescuento(productoDTO.getPrecio(), clienteDTO);
        Double precioConvertidoAPesos = ConversorMoneda.convertirPrecio(precioFinalConDescuento);
        ventaDTO.setPrecioProducto(precioConvertidoAPesos);
        return ventaDTO;
    }
    /**
     * Busca una venta por su ID.
     *
     * @param id ID de la venta a buscar.
     * @return VentaDTO con los detalles de la venta encontrada.
     * @throws NegocioException si no se encuentra una venta con el ID proporcionado.
     */
    public VentaDTO findById(Long id){
        log.info("Buscando venta con ID {}", id);
        Venta ventaEntity = ventaRepository.findById(id).orElseThrow(() -> {
            log.error("Venta no encontrada con ID {}", id);
            return new NegocioException("Venta no existe");
        });
        return ventaMapper.toVentaDTO(ventaEntity);
    }

    /**
     * Crea una nueva venta, realizando todas las validaciones necesarias, aplicando descuentos,
     * y descontando stock del producto.
     *
     * @param idProducto ID del producto a vender.
     * @param idCliente  ID del cliente que realiza la compra.
     * @param formaDePago ID de la forma de pago.
     * @return VentaDTO con los detalles de la venta creada.
     * @throws IOException si ocurre un error en el proceso.
     */
    public VentaDTO crearVenta(Long idProducto, Long idCliente, String formaDePago) throws IOException {
        log.info("Iniciando creación de venta para el cliente ID {} y producto ID {}", idCliente, idProducto);
        validarDatosVenta(idCliente, idProducto);
        ClienteDTO clienteDTO= clienteService.buscarPorID(idCliente);
        ProductoDTO productoDTO=productoService.findById(idProducto);
        VentaDTO ventaDTO = prepararVentaDTO(clienteDTO, productoDTO, formaDePago);
        Venta ventaEntity = ventaRepository.save(ventaMapper.toVentaEntity(ventaDTO));
        productoService.descontarStock(idProducto);
        emailService.enviarFacturaPorEmail(ventaDTO);
        log.info("Venta creada y guardada con éxito para el cliente ID {}", idCliente);
        return ventaMapper.toVentaDTO(ventaEntity);
    }
    /**
     * Valida la forma de pago proporcionada.
     *
     * @param formaDePago la forma de pago a validar.
     * @throws NegocioException si la forma de pago no es válida.
     */
    private void validarFormaPago(String formaDePago) throws NegocioException {
        log.debug("Validando forma de pago: {}", formaDePago);
        boolean isValid = Arrays.stream(FormaPago.values())
                .anyMatch(forma -> forma.name().equalsIgnoreCase(formaDePago));
        if (!isValid) {
            log.error("Forma de pago invalida");
            throw new NegocioException("Forma de pago inválida. Debe ser una de las siguientes: " +
                    Arrays.toString(FormaPago.values()));
        }
        log.info("Forma de pago '{}' es válida.", formaDePago);
    }
    /**
     * Recupera todas las ventas y las convierte a una lista de VentaDTO.
     *
     * @return una lista de objetos VentaDTO que representan todas las ventas.
     */
    public List<VentaDTO> findAll(){
        log.debug("Recuperando todas las ventas.");
        List<VentaDTO> ventasDTO = ventaMapper.toVentaDTOList(ventaRepository.findAll());
        log.info("Se recuperaron {} ventas.", ventasDTO.size());
        return ventasDTO;
    }

    /**
     * Filtra las ventas según los criterios proporcionados en el filtro.
     *
     * @param filtro el objeto FiltroVentaDTO que contiene los criterios de filtrado.
     * @return una lista de objetos VentaDTO que cumplen con los criterios de filtrado.
     * @throws NegocioException si el filtro es nulo o si hay problemas de validación de fechas.
     */
    public List<VentaDTO> filtrarVentas(FiltroVentaDTO filtro) {
        log.debug("Filtrando ventas con los criterios: {}", filtro);
        validarNoNulo(filtro);
        validarFiltros(filtro);
        LocalDateTime[] fechas = validarFechas(filtro.getFechaDesde(), filtro.getFechaHasta());
        LocalDateTime fechaInicio = fechas[0];
        LocalDateTime fechaFin = fechas[1];
        List<Venta> ventasFiltradas = ventaRepository.filtrarVentas(filtro.getNombreCliente(), filtro.getIdCliente(), fechaInicio, fechaFin);
        log.info("Se filtraron {} ventas.", ventasFiltradas.size());
        return ventaMapper.toVentaDTOList(ventasFiltradas);
    }

    /**
     * Valida que las fechas proporcionadas sean correctas y estén en el formato esperado.
     *
     * @param fechaDesde la fecha de inicio en formato de cadena.
     * @param fechaHasta la fecha de fin en formato de cadena.
     * @return un array de LocalDateTime donde el primer elemento es la fecha de inicio
     *         y el segundo es la fecha de fin, ambos convertidos a LocalDateTime.
     * @throws NegocioException si las fechas no son válidas o están en un formato incorrecto.
     */
    private LocalDateTime[] validarFechas(String fechaDesde, String fechaHasta) throws NegocioException {
        log.debug("Validando fechas: desde={}, hasta={}", fechaDesde, fechaHasta);
        if (fechaHasta == null && fechaDesde == null) {
            return new LocalDateTime[]{null, null};
        }
        if (fechaDesde == null || fechaHasta == null) {
            log.error("Ambas fechas deben estar presentes");
            throw new NegocioException("Ambas fechas deben estar presentes para realizar el filtro.");
        }
        try {
            LocalDate fechaInicio = LocalDate.parse(fechaDesde);
            LocalDate fechaFin = LocalDate.parse(fechaHasta);
            if (fechaInicio.isAfter(fechaFin)) {
                log.error("La fecha de inicio no puede ser posterior a la de fin");
                throw new NegocioException("La fecha de inicio no puede ser posterior a la fecha de fin.");
            }
            LocalDateTime inicio = fechaInicio.atStartOfDay();
            LocalDateTime fin = fechaFin.atTime(23, 59, 59);
            return new LocalDateTime[]{inicio, fin};
        } catch (DateTimeParseException e) {
            log.error("Formato fecha invalido");
            throw new NegocioException("Formato de fecha inválido. Debe ser YYYY-MM-DD.");
        }
    }

    /**
     * Valida que al menos uno de los filtros en el objeto FiltroVentaDTO no sea nulo.
     *
     * @param filtro el objeto FiltroVentaDTO a validar.
     * @throws NegocioException si todos los filtros son nulos.
     */
    private void validarNoNulo(FiltroVentaDTO filtro){
        if (filtro.getNombreCliente() == null && filtro.getFechaDesde() == null && filtro.getFechaHasta() == null && filtro.getIdCliente()==null) {
            log.error("Todos los filtros no deben ser nulos");
            throw new NegocioException("Todos los filtros no deben ser nulos");
        }
    }

    private void validarFiltros(FiltroVentaDTO filtro) throws NegocioException {
        log.debug("Validando filtros: nombreCliente={}, idCliente={}", filtro.getNombreCliente(), filtro.getIdCliente());
        if (filtro.getNombreCliente() != null && filtro.getIdCliente() != null) {
            log.error("Solo se permite usar uno de los filtros");
            throw new NegocioException("Solo se permite usar uno de los filtros: 'nombreCliente' o 'idCliente'.");
        }
    }

}
