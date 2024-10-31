package ar.edu.unju.fi.poo.tp8poo;


import ar.edu.unju.fi.poo.tp8poo.dto.*;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ClienteNoActivoException;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ProductoSinStockException;
import ar.edu.unju.fi.poo.tp8poo.service.ClienteService;
import ar.edu.unju.fi.poo.tp8poo.service.ProductoService;
import ar.edu.unju.fi.poo.tp8poo.service.VentaService;
import ar.edu.unju.fi.poo.tp8poo.util.ConversorMoneda;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoProducto;
import ar.edu.unju.fi.poo.tp8poo.util.FormaPago;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

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

    static ClienteEstandarDTO clienteEstandarDTO;
    static ClientePremiumDTO clientePremiumDTO;
    static  ProveedorDTO proveedorDTO;
    static ProductoDTO productoDTO;


    @BeforeEach
    void setUp() {

        clienteEstandarDTO= new ClienteEstandarDTO();
        clienteEstandarDTO.setApellido("Lopez");
        clienteEstandarDTO.setNombre("Raul");
        clienteEstandarDTO.setCelular("1234561341");
        clienteEstandarDTO.setCupon(new CuponDTO(null,"2024-12-02", 10));
        clienteEstandarDTO.setEmail("44351449@fi.unju.edu.ar");
        clienteEstandarDTO.setFoto("https://drive.google.com/uc?id=1SYGQFHAOJmU60I2V-zCsefMtam0tkTjg");
        clienteEstandarDTO.setEstado(EstadoCliente.ACTIVO.name());

        clientePremiumDTO= new ClientePremiumDTO();
        clientePremiumDTO.setApellido("Martinez");
        clientePremiumDTO.setNombre("Maria");
        clientePremiumDTO.setCelular("6542342321");
        clientePremiumDTO.setEmail("maria@hotmail.com");
        clientePremiumDTO.setFoto("https://drive.google.com/uc?id=1Mvv0XIqmdgTg3_qG0-jurVnifKHrMiLz");
        clientePremiumDTO.setEstado(EstadoCliente.ACTIVO.name());
        clientePremiumDTO.setPorcentajeDescuento(10.0);

        proveedorDTO= new ProveedorDTO(null,"proveedor1","proveedor@gmail.com","388453213",true);
        productoDTO = new ProductoDTO(null,"PROD01","producto 1","descripcion prod1", 100.0,5,"url", EstadoProducto.DISPONIBLE.getEstado(), proveedorDTO);
    }

    @AfterEach
    void tearDown() {
        clienteEstandarDTO = null;
        clientePremiumDTO = null;
        proveedorDTO = null;
        productoDTO = null;
    }

    @Test
    public void testCrearVentaClienteEstandarCorrectamenteConDescuento() throws IOException {
        clienteEstandarDTO= clienteService.agregarClienteEstandar(clienteEstandarDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO= ventaService.crearVenta(productoDTO.getId(),clienteEstandarDTO.getId(),FormaPago.CREDITO.name());
        assertNotNull(ventaDTO);
        assertEquals(clienteEstandarDTO.getId(),ventaDTO.getCliente().getId());
    }
    @Test
    public void testCrearVentaClientePremiumCorrectamenteConDescuento() throws IOException {
        clientePremiumDTO= clienteService.agregarClientePremium(clientePremiumDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO=ventaService.crearVenta(productoDTO.getId(), clientePremiumDTO.getId(), FormaPago.TRANSFERENCIA.name());
        assertNotNull(ventaDTO);
        assertEquals(clientePremiumDTO.getId(),ventaDTO.getCliente().getId());
    }
    @Test
    public void testCrearVentaClienteInvalido(){
        clienteEstandarDTO.setEstado(EstadoCliente.INACTIVO.name());
        clienteEstandarDTO=clienteService.agregarClienteEstandar(clienteEstandarDTO);
        productoDTO=productoService.createProducto(productoDTO);
        assertThrows(ClienteNoActivoException.class,()-> ventaService.crearVenta(productoDTO.getId(), clienteEstandarDTO.getId(), FormaPago.DEBITO.name()));
    }

    @Test
    public void testCrearVentaProductoSinStock() {
        clientePremiumDTO = clienteService.agregarClientePremium(clientePremiumDTO);
        productoDTO.setCantidad(0);
        productoDTO=productoService.createProducto(productoDTO);
        assertThrows(ProductoSinStockException.class,()-> ventaService.crearVenta(productoDTO.getId(), clientePremiumDTO.getId(), FormaPago.TRANSFERENCIA.name()));
    }

    @Test
    public void testCrearVentaCuponExpiradoClienteEstandar() throws IOException {
        clienteEstandarDTO.getCupon().setFechaExpiracion("2024-09-10");
        clienteEstandarDTO= clienteService.agregarClienteEstandar(clienteEstandarDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO= ventaService.crearVenta(productoDTO.getId(),clienteEstandarDTO.getId(),FormaPago.CREDITO.name());
        Double preciofinalSinDescuento= ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(preciofinalSinDescuento,ventaDTO.getPrecioProducto());
    }
    @Test
    public void testCrearVentaCuponNullClienteEstandar() throws IOException {
        clienteEstandarDTO.setCupon(null);
        clienteEstandarDTO= clienteService.agregarClienteEstandar(clienteEstandarDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO= ventaService.crearVenta(productoDTO.getId(),clienteEstandarDTO.getId(),FormaPago.CREDITO.name());
        Double preciofinalSinDescuento= ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(preciofinalSinDescuento,ventaDTO.getPrecioProducto());
    }

    @Test
    public void testCrearVentaPorcentajeDescuentoInvalidoClientePremium() throws IOException {
        clientePremiumDTO.setPorcentajeDescuento(150.0);
        clientePremiumDTO= clienteService.agregarClientePremium(clientePremiumDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO= ventaService.crearVenta(productoDTO.getId(),clientePremiumDTO.getId(),FormaPago.CREDITO.name());
        Double preciofinalSinDescuento= ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(preciofinalSinDescuento,ventaDTO.getPrecioProducto());
    }
    @Test
    public void testCrearVentaPorcentajeDescuentoNullClientePremium() throws IOException {
        clientePremiumDTO.setPorcentajeDescuento(null);
        clientePremiumDTO= clienteService.agregarClientePremium(clientePremiumDTO);
        productoDTO=productoService.createProducto(productoDTO);
        VentaDTO ventaDTO= ventaService.crearVenta(productoDTO.getId(),clientePremiumDTO.getId(),FormaPago.CREDITO.name());
        Double preciofinalSinDescuento= ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(preciofinalSinDescuento,ventaDTO.getPrecioProducto());
    }








}
