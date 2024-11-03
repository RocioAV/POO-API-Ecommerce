package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.*;
import ar.edu.unju.fi.poo.tp8poo.entity.Venta;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.mapper.VentaMapper;
import ar.edu.unju.fi.poo.tp8poo.repository.VentaRepository;
import ar.edu.unju.fi.poo.tp8poo.util.ConversorMoneda;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
        ventaDTO.setFormaPago(formaDePago);
        Double precioFinalConDescuento = descuentoService.aplicarDescuento(productoDTO.getPrecio(), clienteDTO);
        Double precioConvertidoAPesos = ConversorMoneda.convertirPrecio(precioFinalConDescuento);
        ventaDTO.setPrecioProducto(precioConvertidoAPesos);
        return ventaDTO;
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

    public List<VentaDTO> findAll(){
        return ventaMapper.toVentaDTOList(ventaRepository.findAll());
    }

    public List<VentaDTO> filtrarVentas(FiltroVentaDTO filtro) {
        List<Venta> ventas = new ArrayList<>();
        validarNoNulo(filtro);
        validarFechas(filtro.getFechaDesde(), filtro.getFechaHasta());
        if (filtro.getNombreCliente() != null) {
            ventas.addAll(ventaRepository.findByClienteNombreContaining(filtro.getNombreCliente()));
        }
        if (filtro.getIdCliente() != null) {
            ventas.addAll(ventaRepository.findByClienteId(filtro.getIdCliente()));
        }
        if (filtro.getFechaDesde() != null && filtro.getFechaHasta() != null) {
            LocalDateTime fechaInicio = LocalDate.parse(filtro.getFechaDesde()).atStartOfDay();
            LocalDateTime fechaFin = LocalDate.parse(filtro.getFechaHasta()).atTime(23, 59, 59);
            ventas.addAll(ventaRepository.findByFechaYHoraBetween(fechaInicio, fechaFin));
        }

        return ventaMapper.toVentaDTOList(ventas);
    }
    private void validarFechas(String fechaDesde, String fechaHasta) throws NegocioException {
        if (fechaDesde == null || fechaHasta == null) {
            throw new NegocioException("Ambas fechas deben estar presentes para realizar el filtro.");
        }
        try {
            LocalDate fechaInicio = LocalDate.parse(fechaDesde);
            LocalDate fechaFin = LocalDate.parse(fechaHasta);
            if (fechaInicio.isAfter(fechaFin)) {
                throw new NegocioException("La fecha de inicio no puede ser posterior a la fecha de fin.");
            }
        } catch (DateTimeParseException e) {
            throw new NegocioException("Formato de fecha inválido. Debe ser YYYY-MM-DD.");
        }
    }

    private void validarNoNulo(FiltroVentaDTO filtro){
        if (filtro.getNombreCliente() == null && filtro.getFechaDesde() == null && filtro.getFechaHasta() == null && filtro.getIdCliente()==null) {
            throw new NegocioException("Todos los filtros no deben ser nulos");
        }
    }

}
