package ar.edu.unju.fi.poo.tp8poo;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.CuponDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.ClienteService;
import ar.edu.unju.fi.poo.tp8poo.testUtil.TestUtils;
import ar.edu.unju.fi.poo.tp8poo.util.enumerated.EstadoCliente;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDate;


@Slf4j
@Transactional
@SpringBootTest
class TestClienteService {

    @Autowired
    ClienteService clienteService;

    static ClienteEstandarDTO clienteEstandarDTO;
    static ClientePremiumDTO clientePremiumDTO;
    static ClienteEstandarDTO clienteEstandarDTOSinCupon;
    static CuponDTO cuponValido;
    static CuponDTO cuponExpirado;
    static CuponDTO cuponValido2;
    static CuponDTO cuponConPorcentajeInvalido;
	MultipartFile multipartFile;
    String workspacePath = System.getProperty("user.dir");
    String rutaArchivo1 = workspacePath + "/src/test/java/ar/edu/unju/fi/poo/tp8poo/img/avatar-test01.jpeg";
    String rutaArchivo2 = workspacePath + "/src/test/java/ar/edu/unju/fi/poo/tp8poo/img/avatar-test02.png";

    @BeforeEach
    public void setUp() {
        clienteEstandarDTO = new ClienteEstandarDTO();
        clienteEstandarDTO.setApellido("Lopez");
        clienteEstandarDTO.setNombre("Raul");
        clienteEstandarDTO.setCelular("1234561341");
        clienteEstandarDTO.setCupon(new CuponDTO(null, "2024-12-02", 10.0));
        clienteEstandarDTO.setEmail("raul5@hotmail.com");
        clienteEstandarDTO.setEstado(EstadoCliente.ACTIVO.name());

        clienteEstandarDTOSinCupon=new ClienteEstandarDTO();
        clienteEstandarDTOSinCupon.setApellido("Amador");
        clienteEstandarDTOSinCupon.setNombre("Cristian");
        clienteEstandarDTOSinCupon.setCelular("987654321");
        clienteEstandarDTOSinCupon.setEmail("ejemplocorreo1@hotmail.com");
        clienteEstandarDTOSinCupon.setEstado(EstadoCliente.ACTIVO.name());

        cuponValido= new CuponDTO(null,LocalDate.now().plusDays(15).toString(),20);
        cuponExpirado=new CuponDTO(null, LocalDate.now().minusDays(1).toString(),45);
        cuponValido2=new CuponDTO(null,LocalDate.now().plusDays(25).toString(),55);
        cuponConPorcentajeInvalido=new CuponDTO(null,LocalDate.now().plusDays(12).toString(),101);

        clientePremiumDTO = new ClientePremiumDTO();
        clientePremiumDTO.setApellido("Martinez");
        clientePremiumDTO.setNombre("Maria");
        clientePremiumDTO.setCelular("6542342321");
        clientePremiumDTO.setEmail("maria@hotmail.com");
        clientePremiumDTO.setEstado(EstadoCliente.ACTIVO.name());
        clientePremiumDTO.setPorcentajeDescuento(20.0); // Descuento para cliente premium
    }

    @Test
     void testAgregarClienteEstandar() throws  IOException{
        ClienteEstandarDTO nuevoCliente = clienteService.agregarClienteEstandar(clienteEstandarDTO);
        multipartFile = TestUtils.generarMultipartFile(rutaArchivo1);
        String url = clienteService.subirImagenCliente(nuevoCliente.getId(), multipartFile);
        assertNotNull(url);
        assertEquals("Raul", nuevoCliente.getNombre());
    }

    @Test
     void testEditarClienteEstandar() throws IOException {
        ClienteEstandarDTO nuevoCliente = clienteService.agregarClienteEstandar(clienteEstandarDTO);
        multipartFile = TestUtils.generarMultipartFile(rutaArchivo2);
        String url= clienteService.subirImagenCliente(nuevoCliente.getId(), multipartFile);
        assertNotNull(url, "La foto debería haber sido establecida");
        ClienteEstandarDTO clienteEstandarEditado = getClienteEstandarDTO(nuevoCliente);
        clienteService.editarClienteEstandar(nuevoCliente.getId(), clienteEstandarEditado);
        ClienteEstandarDTO result = clienteService.getClienteEstandar(nuevoCliente.getId());
        assertEquals("Daniel", result.getNombre());
        assertEquals(nuevoCliente.getFoto(), result.getFoto()); // Verificar que la foto sigue siendo la mi
    }

    private static ClienteEstandarDTO getClienteEstandarDTO(ClienteEstandarDTO nuevoCliente) {
        ClienteEstandarDTO clienteEstandarEditado = new ClienteEstandarDTO();
        clienteEstandarEditado.setApellido("Lopez");
        clienteEstandarEditado.setNombre("Daniel");
        clienteEstandarEditado.setCelular("1233123456");
        clienteEstandarEditado.setEmail("raul@hotmail.com");
        clienteEstandarEditado.setFoto(nuevoCliente.getFoto()); // Mantener la URL de la foto actual
        clienteEstandarEditado.setEstado(EstadoCliente.ACTIVO.name());
        clienteEstandarEditado.setCupon(clienteEstandarDTO.getCupon());
        return clienteEstandarEditado;
    }

    @Test
     void testEliminarLogicamenteClienteEstandar() {
        ClienteEstandarDTO nuevoCliente =  clienteService.agregarClienteEstandar(clienteEstandarDTO);

        clienteService.eliminarLogicamente(nuevoCliente.getId());

        ClienteEstandarDTO clienteEliminado = (ClienteEstandarDTO) clienteService.buscarPorID(nuevoCliente.getId());
        assertNotNull(clienteEliminado);
        assertEquals(EstadoCliente.INACTIVO.name(), clienteEliminado.getEstado(), "El cliente debería estar inactivo.");
    }

    @Test
     void testAgregarClientePremium() throws IOException{
        ClientePremiumDTO nuevoCliente = clienteService.agregarClientePremium(clientePremiumDTO);
        multipartFile = TestUtils.generarMultipartFile(rutaArchivo2);
        String url= clienteService.subirImagenCliente(nuevoCliente.getId(), multipartFile);
        assertNotNull(url);
        assertEquals("Maria", nuevoCliente.getNombre());
    }

    @Test
    void testEditarClientePremium() {
        clientePremiumDTO = clienteService.agregarClientePremium(clientePremiumDTO);
        assertNotNull(clientePremiumDTO.getFoto(), "La foto debería haber sido establecida");
        ClientePremiumDTO clientePremiumEditado = new ClientePremiumDTO();
        clientePremiumEditado.setApellido("Martinez");
        clientePremiumEditado.setNombre("Ana");
        clientePremiumEditado.setCelular("654321");
        clientePremiumEditado.setEmail("mariaawfdf@hotmail.com");
        clientePremiumEditado.setFoto(clientePremiumDTO.getFoto());
        clientePremiumEditado.setEstado(EstadoCliente.ACTIVO.name());
        clientePremiumEditado.setPorcentajeDescuento(20.0);
        clienteService.editarClientePremium(clientePremiumDTO.getId(), clientePremiumEditado);
        ClientePremiumDTO result = clienteService.getClientePremium(clientePremiumDTO.getId());
        assertEquals("Ana", result.getNombre());
        assertEquals(clientePremiumDTO.getFoto(), result.getFoto());
    }


    @Test
     void testAgregarClienteEstandar_EmailDuplicado() {
        clienteService.agregarClienteEstandar(clienteEstandarDTO);

        ClienteEstandarDTO clienteEstandarDTO2 = new ClienteEstandarDTO();
        clienteEstandarDTO2.setApellido("Gonzalez");
        clienteEstandarDTO2.setNombre("Pedro");
        clienteEstandarDTO2.setCelular("987654");
        clienteEstandarDTO2.setEmail("raul5@hotmail.com"); // Mismo email que el primer cliente
        clienteEstandarDTO2.setFoto("https://drive.google.com/uc?id=1Mvv0XIqmdgTg3_qG0-jurVnifKHrMiLz");
        clienteEstandarDTO2.setEstado(EstadoCliente.ACTIVO.name());
        NegocioException exception = assertThrows(NegocioException.class, () -> {
            clienteService.agregarClienteEstandar(clienteEstandarDTO2);
        });

        assertEquals("El cliente con dicho correo ya existe", exception.getMessage());
    }

    @Test
     void testAgregarClienteEstandar_CelularDuplicado() {
        clienteService.agregarClienteEstandar(clienteEstandarDTO);

        ClienteEstandarDTO clienteEstandarDTO2 = new ClienteEstandarDTO();
        clienteEstandarDTO2.setApellido("Gonzalez");
        clienteEstandarDTO2.setNombre("Pedro");
        clienteEstandarDTO2.setCelular("1234561341"); // Mismo celular que el primer cliente
        clienteEstandarDTO2.setEmail("pedro@gmail.com");
        clienteEstandarDTO2.setFoto("https://drive.google.com/uc?id=1nB1VhKuCFmO6jhiAqiPmUWZo1Iwgm8Fy");
        clienteEstandarDTO2.setEstado(EstadoCliente.ACTIVO.name());

        NegocioException exception= assertThrows(NegocioException.class, () -> {
            clienteService.agregarClienteEstandar(clienteEstandarDTO2);
        });
        assertEquals("El cliente con dicho número de celular ya existe", exception.getMessage());
    }

    @Test
     void testAgregarClientePremium_CelularDuplicado() {
        clienteService.agregarClienteEstandar(clienteEstandarDTO);

        ClientePremiumDTO clientePremiumDTO2 = new ClientePremiumDTO();
        clientePremiumDTO2.setApellido("Garcia");
        clientePremiumDTO2.setNombre("Luis");
        clientePremiumDTO2.setCelular("1234561341"); // Mismo celular que el primer cliente
        clientePremiumDTO2.setEmail("luis@gmail.com");
        clientePremiumDTO2.setFoto("https://drive.google.com/uc?id=1nB1VhKuCFmO6jhiAqiPmUWZo1Iwgm8Fy");
        clientePremiumDTO2.setEstado(EstadoCliente.ACTIVO.name());
        clientePremiumDTO2.setPorcentajeDescuento(50.0);

        NegocioException exception = assertThrows(NegocioException.class, () -> {
            clienteService.agregarClientePremium(clientePremiumDTO2);
        });

        assertEquals("El cliente con dicho número de celular ya existe", exception.getMessage());
    }

    @Test
    void testListarClientes(){
        clienteService.agregarClienteEstandar(clienteEstandarDTO);
        clienteService.agregarClientePremium(clientePremiumDTO);
        assertEquals(4,clienteService.obtenerClientes().size());

    }

    @Test
    void testAsignarNuevoCupon_SinCuponExistente() {
        ClienteEstandarDTO clienteGuardado = clienteService.agregarClienteEstandar(clienteEstandarDTOSinCupon);

        clienteService.asignarCupon(clienteGuardado.getId(), cuponValido);

        ClienteEstandarDTO clienteActualizado = clienteService.getClienteEstandar(clienteGuardado.getId());

        assertNotNull(clienteActualizado.getCupon());
        assertEquals(20, clienteActualizado.getCupon().getPorcentajeDescuento());
    }

    @Test
    void testAsignarNuevoCupon_ConCuponExpirado() {
        ClienteEstandarDTO clienteGuardado = clienteService.agregarClienteEstandar(clienteEstandarDTOSinCupon);

        NegocioException exception = assertThrows(NegocioException.class,
                () -> clienteService.asignarCupon(clienteGuardado.getId(), cuponExpirado));

        assertEquals("La fecha de expiración del cupón debe ser posterior a la fecha actual.", exception.getMessage());

        boolean result = clienteService.asignarCupon(clienteGuardado.getId(), cuponValido);
        assertTrue(result, "El cupón debería asignarse porque el actual está expirado.");

        ClienteEstandarDTO clienteEstandar = clienteService.getClienteEstandar(clienteGuardado.getId());
        assertNotNull(clienteEstandar.getCupon(), "El cliente debería tener un nuevo cupón asignado.");
        assertEquals(20, clienteEstandar.getCupon().getPorcentajeDescuento(), "El porcentaje de descuento debe ser 20.");
        assertEquals(cuponValido.getFechaExpiracion(), clienteEstandar.getCupon().getFechaExpiracion().toString(),
                "La fecha de expiración del cupón debería coincidir con la asignada.");
    }

    @Test
    void testNoAsignarCupon_CuponValidoExistente() {
        ClienteEstandarDTO clienteGuardado=clienteService.agregarClienteEstandar(clienteEstandarDTOSinCupon);
        clienteService.asignarCupon(clienteGuardado.getId(), cuponValido);
        assertFalse(clienteService.asignarCupon(clienteGuardado.getId(), cuponValido2));
    }



    @Test
    void testNoAsignarCupon_PorcentajeDescuentoInvalido() {
        ClienteEstandarDTO clienteGuardado=clienteService.agregarClienteEstandar(clienteEstandarDTOSinCupon);

        NegocioException exception = assertThrows(NegocioException.class, () -> {
            clienteService.asignarCupon(clienteGuardado.getId(), cuponConPorcentajeInvalido);
        });
        assertEquals("El porcentaje de descuento debe estar entre 0 y 100.", exception.getMessage());
    }




}

