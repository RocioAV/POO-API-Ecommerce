package ar.edu.unju.fi.poo.tp8poo;


import ar.edu.unju.fi.poo.tp8poo.dto.*;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.ClienteService;
import ar.edu.unju.fi.poo.tp8poo.service.ProductoService;
import ar.edu.unju.fi.poo.tp8poo.service.ProveedorService;
import ar.edu.unju.fi.poo.tp8poo.service.VentaService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

    static ClienteEstandarDTO clienteEstandarDTO;
    static ClientePremiumDTO clientePremiumDTO;
    static  ProveedorDTO proveedorDTO;
    static ProductoDTO productoDTO;
    static CuponDTO cuponDTO;
    File file;
	FileInputStream inputStream;
	MultipartFile multipartFile;
    String workspacePath = System.getProperty("user.dir");
    String rutaArchivo1 = workspacePath + "/src/test/java/ar/edu/unju/fi/poo/tp8poo/img/avatar-test01.jpeg";
    String rutaArchivo2 = workspacePath + "/src/test/java/ar/edu/unju/fi/poo/tp8poo/img/avatar-test02.png";


    @BeforeEach
    void setUp() throws IOException {
        cuponDTO=new CuponDTO(null,"2024-12-02", 10);
        clienteEstandarDTO= new ClienteEstandarDTO(cuponDTO);
        clienteEstandarDTO.setApellido("Lopez");
        clienteEstandarDTO.setNombre("Raul");
        clienteEstandarDTO.setCelular("1234561341");
        clienteEstandarDTO.setEmail("leoalevalle2014@gmail.com");
        multipartFile = generarMultipartFile(rutaArchivo1);
		clienteEstandarDTO.setImagen(multipartFile);
        clienteEstandarDTO.setEstado(EstadoCliente.ACTIVO.name());
        clienteEstandarDTO= clienteService.agregarClienteEstandar(clienteEstandarDTO);

        clientePremiumDTO= new ClientePremiumDTO(10.0);
        clientePremiumDTO.setApellido("Martinez");
        clientePremiumDTO.setNombre("Maria");
        clientePremiumDTO.setCelular("6542342321");
        clientePremiumDTO.setEmail("maria@hotmail.com");
        multipartFile = generarMultipartFile(rutaArchivo2);
		clientePremiumDTO.setImagen(multipartFile);
        clientePremiumDTO.setEstado(EstadoCliente.ACTIVO.name());
        clientePremiumDTO= clienteService.agregarClientePremium(clientePremiumDTO);

        proveedorDTO= new ProveedorDTO(null,"proveedor1","proveedor@gmail.com","388453213",true);
        proveedorDTO=proveedorService.crearProveedor(proveedorDTO);

        productoDTO = new ProductoDTO(null,"PROD01","producto 1","descripcion prod1", 100.0,5,"url", EstadoProducto.DISPONIBLE.getEstado(),proveedorDTO.getId());

        productoDTO=productoService.createProducto(productoDTO);

    }

    @AfterEach
    void tearDown() {
        clienteEstandarDTO = null;
        clientePremiumDTO = null;
        proveedorDTO = null;
        productoDTO = null;
        cuponDTO=null;
    }

    @Test
     void testCrearVentaClienteEstandarCorrectamenteConDescuento() throws IOException {
        VentaDTO ventaDTO= ventaService.crearVenta(productoDTO.getId(),clienteEstandarDTO.getId(),FormaPago.CREDITO.name());
        assertNotNull(ventaDTO);
        assertEquals(clienteEstandarDTO.getId(),ventaDTO.getCliente().getId());
    }
    @Test
     void testCrearVentaClientePremiumCorrectamenteConDescuento() throws IOException {
        VentaDTO ventaDTO=ventaService.crearVenta(productoDTO.getId(), clientePremiumDTO.getId(), FormaPago.TRANSFERENCIA.name());
        assertNotNull(ventaDTO);
        assertEquals(clientePremiumDTO.getId(),ventaDTO.getCliente().getId());
    }
    @Test
     void testCrearVentaClienteInvalido(){
        clienteEstandarDTO.setEstado(EstadoCliente.INACTIVO.name());
        clienteEstandarDTO=clienteService.editarClienteEstandar(clienteEstandarDTO.getId(),clienteEstandarDTO);
        NegocioException exception = assertThrows(NegocioException.class,()->{
            ventaService.crearVenta(productoDTO.getId(), clienteEstandarDTO.getId(), FormaPago.DEBITO.name());
        });
        assertEquals("El cliente no esta activo para hacer una compra", exception.getMessage());
    }

    @Test
     void testCrearVentaProductoSinStock() {
        productoDTO.setCantidad(0);
        productoDTO=productoService.editProducto(productoDTO.getId(),productoDTO);
        NegocioException exception=assertThrows(NegocioException.class,()-> {
            ventaService.crearVenta(productoDTO.getId(), clientePremiumDTO.getId(), FormaPago.TRANSFERENCIA.name());
        });
        assertEquals("El producto NO tiene stock",exception.getMessage());
    }

    @Test
     void testCrearVentaCuponExpiradoClienteEstandar() throws IOException {
        clienteEstandarDTO.getCupon().setFechaExpiracion("2024-09-10");
        clienteEstandarDTO= clienteService.editarClienteEstandar(clienteEstandarDTO.getId(),clienteEstandarDTO);
        VentaDTO ventaDTO= ventaService.crearVenta(productoDTO.getId(),clienteEstandarDTO.getId(),FormaPago.CREDITO.name());
        Double preciofinalSinDescuento= ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(preciofinalSinDescuento,ventaDTO.getPrecioProducto());
    }
    @Test
     void testCrearVentaCuponNullClienteEstandar() throws IOException {
        clienteEstandarDTO.setCupon(null);
        System.out.println(clienteEstandarDTO.getCupon());
        clienteEstandarDTO= clienteService.editarClienteEstandar(clienteEstandarDTO.getId(),clienteEstandarDTO);
        System.out.println(clienteEstandarDTO);
        VentaDTO ventaDTO= ventaService.crearVenta(productoDTO.getId(),clienteEstandarDTO.getId(),FormaPago.CREDITO.name());
        Double preciofinalSinDescuento= ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(preciofinalSinDescuento,ventaDTO.getPrecioProducto());
    }

    @Test
     void testCrearVentaPorcentajeDescuentoInvalidoClientePremium() throws IOException {
        clientePremiumDTO.setPorcentajeDescuento(150.0);
        clientePremiumDTO= clienteService.editarClientePremium(clientePremiumDTO.getId(),clientePremiumDTO);
        VentaDTO ventaDTO= ventaService.crearVenta(productoDTO.getId(),clientePremiumDTO.getId(),FormaPago.CREDITO.name());
        Double preciofinalSinDescuento= ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(preciofinalSinDescuento,ventaDTO.getPrecioProducto());
    }
    @Test
     void testCrearVentaPorcentajeDescuentoNullClientePremium() throws IOException {
        clientePremiumDTO.setPorcentajeDescuento(null);
        clientePremiumDTO= clienteService.editarClientePremium(clientePremiumDTO.getId(),clientePremiumDTO);
        VentaDTO ventaDTO= ventaService.crearVenta(productoDTO.getId(),clientePremiumDTO.getId(),FormaPago.CREDITO.name());
        Double preciofinalSinDescuento= ConversorMoneda.convertirPrecio(productoDTO.getPrecio());
        assertEquals(preciofinalSinDescuento,ventaDTO.getPrecioProducto());
    }

    private MultipartFile generarMultipartFile(String rutaArchivo) throws IOException {
    	String nombreArchivo = rutaArchivo.substring(rutaArchivo.lastIndexOf("/") + 1);
    	String tipoContenido = "image/" + rutaArchivo.substring(rutaArchivo.lastIndexOf(".") + 1);
    	log.debug("generando MultipartFilenombre del archivo: {}", nombreArchivo);

    	// Lee una imagen real del sistema de archivos
    	inputStream = new FileInputStream(rutaArchivo);
    	
    	// Crear un MockMultipartFile con una imagen real
        MultipartFile multipartFile = new MockMultipartFile(
                "file", // Nombre del parámetro
                nombreArchivo, // Nombre del archivo
                tipoContenido, // Tipo de contenido (MIME)
                inputStream); // Contenido del archivo
        
        return multipartFile;
    }

}
