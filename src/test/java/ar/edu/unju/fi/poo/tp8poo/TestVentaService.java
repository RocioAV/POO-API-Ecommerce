package ar.edu.unju.fi.poo.tp8poo;


import ar.edu.unju.fi.poo.tp8poo.dto.*;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ClienteNoActivoException;
import ar.edu.unju.fi.poo.tp8poo.exceptions.DebitoVencidaException;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ProductoSinStockException;
import ar.edu.unju.fi.poo.tp8poo.service.ClienteService;
import ar.edu.unju.fi.poo.tp8poo.service.ProductoService;
import ar.edu.unju.fi.poo.tp8poo.service.ProveedorService;
import ar.edu.unju.fi.poo.tp8poo.service.VentaService;
import ar.edu.unju.fi.poo.tp8poo.util.ConversorMoneda;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoProducto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class TestVentaService {

    @Autowired
    VentaService ventaService;
    @Autowired
    ClienteService clienteService;
    @Autowired
    ProductoService productoService;

    static PagoDebitoDTO pagoDebitoDTO;
    static PagoTransferenciaDTO pagoTransferenciaDTO;
    static ClienteEstandarDTO clienteEstandarDTO;
    static ClientePremiumDTO clientePremiumDTO;
    static  ProveedorDTO proveedorDTO;
    static ProductoDTO productoDTO;


    @BeforeEach
    void setUp() {
        pagoDebitoDTO= new PagoDebitoDTO();
        pagoDebitoDTO.setNombreTitular("ro");
        pagoDebitoDTO.setApellidoTitular("velazke");
        pagoDebitoDTO.setNroTarjeta("123456789456");
        pagoDebitoDTO.setCodigoSeguridad(321);
        pagoDebitoDTO.setMesVencimiento(12);
        pagoDebitoDTO.setAnioVencimiento(2026);

        pagoTransferenciaDTO= new PagoTransferenciaDTO();
        pagoTransferenciaDTO.setCbuDestino("123123456784569");
        pagoTransferenciaDTO.setCuil("27443514495");
        pagoTransferenciaDTO.setEntidadBancariaOrigen("brubank");
        pagoTransferenciaDTO.setNroTransferencia("000001");

        clienteEstandarDTO= new ClienteEstandarDTO();
        clienteEstandarDTO.setApellido("Lopez");
        clienteEstandarDTO.setNombre("Raul");
        clienteEstandarDTO.setCelular("1234561341");
        clienteEstandarDTO.setCupon(new CuponDTO(null, LocalDate.of(2024, 12, 2), 10));
        clienteEstandarDTO.setCreated(LocalDateTime.now());
        clienteEstandarDTO.setEmail("raul5@hotmail.com");
        clienteEstandarDTO.setFoto("https://drive.google.com/uc?id=1SYGQFHAOJmU60I2V-zCsefMtam0tkTjg");
        clienteEstandarDTO.setUpdated(null);
        clienteEstandarDTO.setEstado(EstadoCliente.ACTIVO.name());

        clientePremiumDTO= new ClientePremiumDTO();
        clientePremiumDTO.setApellido("Martinez");
        clientePremiumDTO.setNombre("Maria");
        clientePremiumDTO.setCelular("6542342321");
        clientePremiumDTO.setCreated(LocalDateTime.now());
        clientePremiumDTO.setEmail("maria@hotmail.com");
        clientePremiumDTO.setFoto("https://drive.google.com/uc?id=1Mvv0XIqmdgTg3_qG0-jurVnifKHrMiLz");
        clientePremiumDTO.setUpdated(null);
        clientePremiumDTO.setEstado(EstadoCliente.ACTIVO.name());
        clientePremiumDTO.setPorcentajeDescuento(10.0);
        proveedorDTO= new ProveedorDTO(null,"proveedor1","proveedor@gmail.com","388453213",true);
        productoDTO = new ProductoDTO(null,"PROD01","producto 1","descripcion prod1", 100.0,5,"url", EstadoProducto.DISPONIBLE.getEstado(), proveedorDTO);
    }

    @AfterEach
    void tearDown() {
        pagoDebitoDTO = null;
        pagoTransferenciaDTO = null;
        clienteEstandarDTO = null;
        clientePremiumDTO = null;
        proveedorDTO = null;
        productoDTO = null;
    }

    @Test
    public void testCrearVentaClienteEstandarCorrectamenteConDescuento() throws IOException {
        clienteEstandarDTO= clienteService.agregarClienteEstandar(clienteEstandarDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO=new VentaDTO();
        ventaDTO.setFormaPago(pagoDebitoDTO);
        ventaDTO= ventaService.crearVenta(productoDTO.getId(),clienteEstandarDTO.getId(),ventaDTO);
        assertNotNull(ventaDTO);
        assertEquals(clienteEstandarDTO.getId(),ventaDTO.getCliente().getId());
    }
    @Test
    public void testCrearVentaClientePremiumCorrectamenteConDescuento() throws IOException {
        clientePremiumDTO= clienteService.agregarClientePremium(clientePremiumDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO=new VentaDTO();
        ventaDTO.setFormaPago(pagoTransferenciaDTO);
        ventaDTO=ventaService.crearVenta(productoDTO.getId(), clientePremiumDTO.getId(), ventaDTO);
        assertNotNull(ventaDTO);
        assertEquals(clientePremiumDTO.getId(),ventaDTO.getCliente().getId());
    }
    @Test
    public void testCrearVentaClienteInvalido(){
        clienteEstandarDTO.setEstado(EstadoCliente.INACTIVO.name());
        clienteEstandarDTO=clienteService.agregarClienteEstandar(clienteEstandarDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO=new VentaDTO();
        ventaDTO.setFormaPago(pagoTransferenciaDTO);
        assertThrows(ClienteNoActivoException.class,()-> ventaService.crearVenta(productoDTO.getId(), clienteEstandarDTO.getId(), ventaDTO));
    }

    @Test
    public void testCrearVentaProductoSinStock() {
        clientePremiumDTO = clienteService.agregarClientePremium(clientePremiumDTO);
        productoDTO.setCantidad(0);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO=new VentaDTO();
        ventaDTO.setFormaPago(pagoTransferenciaDTO);
        assertThrows(ProductoSinStockException.class,()-> ventaService.crearVenta(productoDTO.getId(), clientePremiumDTO.getId(), ventaDTO));
    }

    @Test
    public void testCrearVentaCuponExpiradoClienteEstandar() throws IOException {
        clienteEstandarDTO.getCupon().setFechaExpiracion(LocalDate.of(2024,9,10));
        clienteEstandarDTO= clienteService.agregarClienteEstandar(clienteEstandarDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO=new VentaDTO();
        ventaDTO.setFormaPago(pagoDebitoDTO);
        ventaDTO= ventaService.crearVenta(productoDTO.getId(),clienteEstandarDTO.getId(),ventaDTO);
        Double preciofinalSinDescuento= ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(preciofinalSinDescuento,ventaDTO.getPrecioProducto());
    }
    @Test
    public void testCrearVentaCuponNullClienteEstandar() throws IOException {
        clienteEstandarDTO.setCupon(null);
        clienteEstandarDTO= clienteService.agregarClienteEstandar(clienteEstandarDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO=new VentaDTO();
        ventaDTO.setFormaPago(pagoDebitoDTO);
        ventaDTO= ventaService.crearVenta(productoDTO.getId(),clienteEstandarDTO.getId(),ventaDTO);
        Double preciofinalSinDescuento= ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(preciofinalSinDescuento,ventaDTO.getPrecioProducto());
    }

    @Test
    public void testCrearVentaPorcentajeDescuentoInvalidoClientePremium() throws IOException {
        clientePremiumDTO.setPorcentajeDescuento(150.0);
        clientePremiumDTO= clienteService.agregarClientePremium(clientePremiumDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO=new VentaDTO();
        ventaDTO.setFormaPago(pagoTransferenciaDTO);
        ventaDTO= ventaService.crearVenta(productoDTO.getId(),clientePremiumDTO.getId(),ventaDTO);
        Double preciofinalSinDescuento= ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(preciofinalSinDescuento,ventaDTO.getPrecioProducto());
    }
    @Test
    public void testCrearVentaPorcentajeDescuentoNullClientePremium() throws IOException {
        clientePremiumDTO.setPorcentajeDescuento(null);
        clientePremiumDTO= clienteService.agregarClientePremium(clientePremiumDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO=new VentaDTO();
        ventaDTO.setFormaPago(pagoTransferenciaDTO);
        ventaDTO= ventaService.crearVenta(productoDTO.getId(),clientePremiumDTO.getId(),ventaDTO);
        Double preciofinalSinDescuento= ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(preciofinalSinDescuento,ventaDTO.getPrecioProducto());
    }

    @Test
    public void testCrearVentaTarjetaDebitoVencida() throws IOException {
        clientePremiumDTO=clienteService.agregarClientePremium(clientePremiumDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO=new VentaDTO();
        pagoDebitoDTO.setAnioVencimiento(2024);
        pagoDebitoDTO.setMesVencimiento(6);
        ventaDTO.setFormaPago(pagoDebitoDTO);
        assertThrows(DebitoVencidaException.class,()-> ventaService.crearVenta(productoDTO.getId(),clientePremiumDTO.getId(),ventaDTO));

    }







}
