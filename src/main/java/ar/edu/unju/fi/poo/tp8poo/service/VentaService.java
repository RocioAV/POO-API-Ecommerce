package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.*;
import ar.edu.unju.fi.poo.tp8poo.entity.Cliente;
import ar.edu.unju.fi.poo.tp8poo.entity.Venta;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ClienteNoActivoException;
import ar.edu.unju.fi.poo.tp8poo.exceptions.DebitoVencidaException;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ProductoSinStockException;
import ar.edu.unju.fi.poo.tp8poo.exceptions.VentaInexistenteException;
import ar.edu.unju.fi.poo.tp8poo.mapper.ClienteMapper;
import ar.edu.unju.fi.poo.tp8poo.mapper.VentaMapper;
import ar.edu.unju.fi.poo.tp8poo.repository.VentaRepository;
import ar.edu.unju.fi.poo.tp8poo.util.ConversorMoneda;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoProducto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Slf4j
@Service
public class VentaService {
    @Autowired
    ClienteService clienteService;

    @Autowired
    ProductoService productoService;

    @Autowired
    VentaRepository ventaRepository;

    @Autowired
    VentaMapper ventaMapper;


    private void validarClienteActivo(Long id){
        log.info("Validando si el cliente con ID {} está activo", id);
        ClienteDTO cliente = clienteService.buscarPorID(id);
        if (!cliente.getEstado().equals(EstadoCliente.ACTIVO.name())) {
            log.warn("El cliente con ID {} no está activo para hacer una compra", id);
            throw new ClienteNoActivoException("El cliente no esta activo para hacer una compra");
        }
    }

    private void validarProductoDisponible(Long id){
        log.info("Validando disponibilidad del producto con ID {}", id);
        ProductoDTO producto= productoService.findById(id);
        if (producto.getCantidad()<=0){
            log.warn("El producto con ID {} no tiene stock", id);
            throw new ProductoSinStockException("El producto NO tiene stock");
        }
    }

    private Double calcularDescuento(Double porcentaje, Double precioProd){
        log.debug("Calculando descuento del {}% sobre el precio {}", porcentaje,precioProd);
        return porcentaje * precioProd / 100;
    }
    private Double verificarDescuentoClientePremiun(ClientePremiumDTO clientePremiumDTO, Double precioProducto){
        log.info("Verificando descuento para cliente premium con ID {}", clientePremiumDTO.getId());
        if(clientePremiumDTO.getPorcentajeDescuento()!=null){
            if (clientePremiumDTO.getPorcentajeDescuento()<= 0.0 || clientePremiumDTO.getPorcentajeDescuento()>100 ){
                log.warn("Porcentaje de descuento inválido para cliente premium");
                return precioProducto;
            }else{
                Double descuento=calcularDescuento(clientePremiumDTO.getPorcentajeDescuento(),precioProducto);
                return precioProducto-descuento;
            }
        }else{
            return precioProducto;
        }

    }
    public boolean isExpirado(LocalDate fechaExpiracion) {
        log.debug("Verificando si el cupón ha expirado para la fecha: {}", fechaExpiracion);
        LocalDate ahora = LocalDate.now();
        return fechaExpiracion.isBefore(ahora);
    }
    private Double verificarDescuentoClienteEstandar(ClienteEstandarDTO clienteEstandarDTO, Double precioProducto){
        log.info("Verificando descuento para cliente estándar con ID {}", clienteEstandarDTO.getId());
        if (clienteEstandarDTO.getCupon()!=null){
            if(!isExpirado(clienteEstandarDTO.getCupon().getFechaExpiracion())){
                Double descuento= calcularDescuento(clienteEstandarDTO.getCupon().getPorcentajeDescuento(),precioProducto);
                return precioProducto-descuento;
            }else {
                log.warn("Cupón expirado para cliente estándar con ID {}", clienteEstandarDTO.getId());
                return precioProducto;
            }
        }else {
            log.warn("No existe cupon para cliente estándar con ID {}", clienteEstandarDTO.getId());
            return precioProducto;
        }
    }
    private Double aplicarDescuento(Double precioProducto, ClienteDTO clienteDTO){
        log.info("Aplicando descuento para cliente con ID {}", clienteDTO.getId());
        Double precioFinal;
        if(clienteDTO instanceof ClientePremiumDTO){
            precioFinal=verificarDescuentoClientePremiun((ClientePremiumDTO) clienteDTO,precioProducto);
        }else{
            precioFinal=verificarDescuentoClienteEstandar((ClienteEstandarDTO)clienteDTO,precioProducto);
        }
        return precioFinal;
    }
    private void descontarStock(ProductoDTO producto){
        log.info("Descontando stock para el producto con ID {}", producto.getId());
        producto.setCantidad(producto.getCantidad()-1);
        if(producto.getCantidad()==0){
            producto.setEstado(EstadoProducto.NO_DISPONIBLE.getEstado());
        }
        productoService.editProducto(producto.getId(), producto);
    }

    private void validarTarjetaDebito(PagoDTO pagoDTO){
        if (pagoDTO instanceof PagoDebitoDTO) {
            PagoDebitoDTO pagoDebito = (PagoDebitoDTO) pagoDTO;
            log.info("Validando fecha de vencimiento para tarjeta de débito: {}/{}", pagoDebito.getMesVencimiento(), pagoDebito.getAnioVencimiento());
            YearMonth fechaActual = YearMonth.now();
            YearMonth fechaVencimiento = YearMonth.of(pagoDebito.getAnioVencimiento(), pagoDebito.getMesVencimiento());
            if (fechaVencimiento.isBefore(fechaActual)) {
                log.error("Tarjeta de débito vencida en {}/{}", pagoDebito.getMesVencimiento(), pagoDebito.getAnioVencimiento());
                throw new DebitoVencidaException("La tarjeta está vencida");
            }
        }
    }

    public VentaDTO crearVenta(Long idProducto,Long idCliente,VentaDTO ventadto) throws IOException {
        log.info("Iniciando creación de venta para el cliente ID {} y producto ID {}", idCliente, idProducto);
        validarClienteActivo(idCliente);
        validarProductoDisponible(idProducto);

        ClienteDTO clienteDTO= clienteService.buscarPorID(idCliente);
        ProductoDTO productoDTO=productoService.findById(idProducto);

        ventadto.setFechaYHora(LocalDateTime.now());
        ventadto.setProducto(productoDTO);
        ventadto.setCliente(clienteDTO);

        Double preciofinal= aplicarDescuento(ventadto.getProducto().getPrecio(),ventadto.getCliente());
        Double precioConvertidoAPesos= ConversorMoneda.convertirPrecio(preciofinal);

        log.debug("Precio final después del descuento: {}", preciofinal);
        ventadto.setPrecioProducto(precioConvertidoAPesos);
        ventadto.getFormaPago().setImporte(precioConvertidoAPesos);

        validarTarjetaDebito(ventadto.getFormaPago());

        Venta ventaEntity =ventaMapper.toVentaEntity(ventadto);
        ventaRepository.save(ventaEntity);

        log.info("Venta creada y guardada con éxito para el cliente ID {}", idCliente);
        descontarStock(ventadto.getProducto());

        return ventaMapper.toVentaDTO(ventaEntity);

    }

    public VentaDTO findById(Long id){
        log.info("Buscando venta con ID {}", id);
        Venta ventaEntity = ventaRepository.findById(id).orElseThrow(() -> {
            log.error("Venta no encontrada con ID {}", id);
            return new VentaInexistenteException("Venta no existe");
        });
        return ventaMapper.toVentaDTO(ventaEntity);
    }



}
