package ar.edu.unju.fi.poo.tp8poo;


import ar.edu.unju.fi.poo.tp8poo.dto.*;
import ar.edu.unju.fi.poo.tp8poo.entity.Token;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.repository.TokenRepository;
import ar.edu.unju.fi.poo.tp8poo.service.*;
import ar.edu.unju.fi.poo.tp8poo.util.ConversorMoneda;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoProducto;
import ar.edu.unju.fi.poo.tp8poo.util.FormaPago;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Transactional
@SpringBootTest
 class TestVentaService {

    @Autowired
    VentaService ventaService;
    @Autowired
    ClienteService clienteService;
    @Autowired
    ProductoService productoService;
    @Autowired
    ProveedorService proveedorService;
    @Autowired
    TokenRepository tokenRepository;

    static ClienteEstandarDTO clienteEstandarDTO;
    static ClientePremiumDTO clientePremiumDTO;
    static  ProveedorDTO proveedorDTO;
    static ProductoDTO productoDTO;
    static CuponDTO cuponDTO;
    static Token tokenValido;
    static Token tokenValido2;
	MultipartFile multipartFile;
    String workspacePath = System.getProperty("user.dir");
    String rutaArchivo1 = workspacePath + "/src/test/java/ar/edu/unju/fi/poo/tp8poo/img/avatar-test01.jpeg";
    String rutaArchivo2 = workspacePath + "/src/test/java/ar/edu/unju/fi/poo/tp8poo/img/avatar-test02.png";


    @BeforeEach
    void setUp()  {
        cuponDTO = new CuponDTO(2L, "2024-12-12", 10);
        clienteEstandarDTO = createClienteEstandar();
        clienteService.asignarCupon(clienteEstandarDTO.getId(), cuponDTO);
        clienteEstandarDTO=clienteService.getClienteEstandar(clienteEstandarDTO.getId());
        tokenValido = new Token("548543", LocalDateTime.now().plusSeconds(300), clienteEstandarDTO.getId());
        tokenRepository.save(tokenValido);

        clientePremiumDTO = createClientePremium();
        tokenValido2 = new Token("584652", LocalDateTime.now().plusSeconds(300), clientePremiumDTO.getId());
        tokenRepository.save(tokenValido2);
        proveedorDTO = new ProveedorDTO(null, "proveedor1", "proveedor@gmail.com", "388453213", true);
        proveedorDTO = proveedorService.crearProveedor(proveedorDTO);
        productoDTO = new ProductoDTO(null, "PROD01", "producto 1", "descripcion prod1", 100.0, 5, "url", EstadoProducto.DISPONIBLE.getEstado(), proveedorDTO.getId());
        productoDTO = productoService.createProducto(productoDTO);
    }

    @AfterEach
    void tearDown() {
        clienteEstandarDTO = null;
        clientePremiumDTO = null;
        proveedorDTO = null;
        productoDTO = null;
        cuponDTO = null;
    }

    private ClienteEstandarDTO createClienteEstandar(){
        ClienteEstandarDTO cliente = new ClienteEstandarDTO();
        cliente.setNombre("Raul");
        cliente.setApellido("Lopez");
        cliente.setCelular("1234561341");
        cliente.setEmail("pepit@gmail.com");
        cliente.setFoto(rutaArchivo1);
        cliente.setEstado(EstadoCliente.ACTIVO.name());
        return clienteService.agregarClienteEstandar(cliente);
    }
    private ClientePremiumDTO createClientePremium() {
        ClientePremiumDTO cliente = new ClientePremiumDTO();
        cliente.setNombre("Maria");
        cliente.setApellido("Martinez");
        cliente.setCelular("6542342321");
        cliente.setEmail("pedrito@gmail.com");
        cliente.setFoto(rutaArchivo2);
        cliente.setEstado(EstadoCliente.ACTIVO.name());
        cliente.setPorcentajeDescuento(10.0);
        return clienteService.agregarClientePremium(cliente);
    }

    @Test
    void testCrearVentaClienteEstandarCorrectamenteConDescuento() throws IOException {
        VentaDTO ventaDTO = ventaService.crearVenta(productoDTO.getId(), clienteEstandarDTO.getId(), FormaPago.CREDITO.name(),tokenValido.getValor());
        assertNotNull(ventaDTO, "La venta no debe ser nula.");
        assertEquals(clienteEstandarDTO.getId(), ventaDTO.getCliente().getId(), "El ID del cliente en la venta no coincide.");
    }

    @Test
    void testCrearVentaClientePremiumCorrectamenteConDescuento() throws IOException {
        VentaDTO ventaDTO = ventaService.crearVenta(productoDTO.getId(), clientePremiumDTO.getId(), FormaPago.TRANSFERENCIA.name(), tokenValido2.getValor());
        assertNotNull(ventaDTO, "La venta no debe ser nula.");
        assertEquals(clientePremiumDTO.getId(), ventaDTO.getCliente().getId(), "El ID del cliente en la venta no coincide.");
    }

    @Test
    void testCrearVentaClienteInvalido() {
        clienteEstandarDTO.setEstado(EstadoCliente.INACTIVO.name());
        clienteEstandarDTO = clienteService.editarClienteEstandar(clienteEstandarDTO.getId(), clienteEstandarDTO);
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            ventaService.crearVenta(productoDTO.getId(), clienteEstandarDTO.getId(), FormaPago.DEBITO.name(), tokenValido.getValor());
        });
        assertEquals("El cliente no esta activo para hacer una compra", exception.getMessage(), "El mensaje de error no es el esperado.");
    }

    @Test
    void testCrearVentaProductoSinStock() {
        productoDTO.setCantidad(0);
        productoDTO = productoService.editProducto(productoDTO.getId(), productoDTO);
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            ventaService.crearVenta(productoDTO.getId(), clientePremiumDTO.getId(), FormaPago.TRANSFERENCIA.name(), tokenValido2.getValor());
        });
        assertEquals("El producto NO tiene stock", exception.getMessage(), "El mensaje de error no es el esperado.");
    }

    @Test
    void testCrearVentaCuponExpiradoClienteEstandar() throws IOException {
        cuponDTO.setFechaExpiracion("2023-12-12");
        clienteService.asignarCupon(clienteEstandarDTO.getId(), cuponDTO);
        VentaDTO ventaDTO = ventaService.crearVenta(productoDTO.getId(), clienteEstandarDTO.getId(), FormaPago.CREDITO.name(),tokenValido.getValor());
        Double precioFinalSinDescuento = ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(precioFinalSinDescuento, ventaDTO.getPrecioProducto(), "El precio final no es el esperado.");
    }

//    @Test
//    void testCrearVentaCuponNullClienteEstandar() throws IOException {
//        cuponDTO= null;
//        clienteService.asignarCupon(clienteEstandarDTO.getId(), cuponDTO);
//        System.out.println(clienteEstandarDTO);
//        VentaDTO ventaDTO = ventaService.crearVenta(productoDTO.getId(), clienteEstandarDTO.getId(), FormaPago.CREDITO.name());
//        Double precioFinalSinDescuento = ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
//        assertEquals(precioFinalSinDescuento, ventaDTO.getPrecioProducto(), "El precio final no es el esperado.");
//    }

    @Test
    void testCrearVentaPorcentajeDescuentoInvalidoClientePremium() throws IOException {
        clientePremiumDTO.setPorcentajeDescuento(150.0);
        clientePremiumDTO = clienteService.editarClientePremium(clientePremiumDTO.getId(), clientePremiumDTO);
        VentaDTO ventaDTO = ventaService.crearVenta(productoDTO.getId(), clientePremiumDTO.getId(), FormaPago.CREDITO.name(), tokenValido2.getValor());
        Double precioFinalSinDescuento = ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(precioFinalSinDescuento, ventaDTO.getPrecioProducto(), "El precio final no es el esperado.");
    }

    @Test
    void testCrearVentaPorcentajeDescuentoNullClientePremium() throws IOException {
        clientePremiumDTO.setPorcentajeDescuento(null);
        clientePremiumDTO = clienteService.editarClientePremium(clientePremiumDTO.getId(), clientePremiumDTO);
        VentaDTO ventaDTO = ventaService.crearVenta(productoDTO.getId(), clientePremiumDTO.getId(), FormaPago.CREDITO.name(), tokenValido2.getValor());
        Double precioFinalSinDescuento = ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(precioFinalSinDescuento, ventaDTO.getPrecioProducto(), "El precio final no es el esperado.");
    }
}
