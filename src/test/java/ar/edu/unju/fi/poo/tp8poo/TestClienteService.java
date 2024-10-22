package ar.edu.unju.fi.poo.tp8poo;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Cliente;
import ar.edu.unju.fi.poo.tp8poo.entity.ClienteEstandar;
import ar.edu.unju.fi.poo.tp8poo.entity.ClientePremium;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;
import ar.edu.unju.fi.poo.tp8poo.exceptions.CelularDuplicadoException;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ClienteExistenteException;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ClienteInexixtenteExcepcion;
import ar.edu.unju.fi.poo.tp8poo.exceptions.EmailDuplicadoException;
import ar.edu.unju.fi.poo.tp8poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.tp8poo.service.ClienteService;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import jakarta.transaction.Transactional;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class TestClienteService {

    @Autowired
    private ClienteService clienteService; // Clase bajo prueba


    @Test
    public void testAgregarClienteEstandar() {
        ClienteEstandarDTO clienteEstandarDTO = new ClienteEstandarDTO();
        clienteEstandarDTO.setApellido("Lopez");
        clienteEstandarDTO.setNombre("Raul");
        clienteEstandarDTO.setCelular("1234561341");
        clienteEstandarDTO.setCupon(new Cupon(4L,LocalDate.of(2024, 12, 2),15));
        clienteEstandarDTO.setId(4L);
        clienteEstandarDTO.setCreated(LocalDateTime.now());
        clienteEstandarDTO.setEmail("raul5@hotmail.com");
        clienteEstandarDTO.setFoto("123");
        clienteEstandarDTO.setUpdated(null);
        clienteEstandarDTO.setEstado(EstadoCliente.ACTIVO.name());

        ClienteEstandarDTO nuevoCliente = clienteService.agregarClienteEstandar(clienteEstandarDTO);
        assertNotNull(nuevoCliente);
        assertEquals("Raul", nuevoCliente.getNombre());
    }

    @Test
    public void testEditarClienteEstandar() {
        Cupon cupon = new Cupon();
        cupon.setFechaExpiracion(LocalDate.of(2024, 12, 2));
        cupon.setPorcentajeDescuento(15);
        ;  // Asocia el cupon al cliente
        ClienteEstandarDTO clienteEstandarDTO = new ClienteEstandarDTO();
        clienteEstandarDTO.setApellido("Lopez");
        clienteEstandarDTO.setNombre("Raul");
        clienteEstandarDTO.setCelular("123456312");
        clienteEstandarDTO.setCreated(LocalDateTime.now());
        clienteEstandarDTO.setEmail("raul1@hotmail.com");
        clienteEstandarDTO.setFoto("123");
        clienteEstandarDTO.setUpdated(null);
        clienteEstandarDTO.setEstado(EstadoCliente.ACTIVO.name());

        clienteEstandarDTO.setCupon(cupon);

        clienteEstandarDTO=clienteService.agregarClienteEstandar(clienteEstandarDTO);

        // Editar cliente
        ClienteEstandarDTO clienteEstandarEditado = new ClienteEstandarDTO();
        clienteEstandarEditado.setApellido("Lopez");
        clienteEstandarEditado.setNombre("Daniel");
        clienteEstandarEditado.setCelular("1233123456");
        clienteEstandarEditado.setCreated(clienteEstandarDTO.getCreated());
        clienteEstandarEditado.setEmail("raul@hotmail.com");
        clienteEstandarEditado.setFoto("123");
        clienteEstandarEditado.setUpdated(LocalDateTime.now());
        clienteEstandarEditado.setEstado(EstadoCliente.ACTIVO.name());

        clienteEstandarEditado.setCupon(clienteEstandarDTO.getCupon());

        clienteService.editarClienteEstandar(clienteEstandarDTO.getId(), clienteEstandarEditado);

        ClienteEstandarDTO result = clienteService.getClienteEstandar(clienteEstandarDTO.getId());
        assertEquals("Daniel", result.getNombre());
    }

    @Test
    public void testEliminarLogicamenteClienteEstandar() {
        // 1. Crear un cupón para el cliente estándar
        Cupon cupon = new Cupon();
        cupon.setPorcentajeDescuento(10.0);
        cupon.setFechaExpiracion(LocalDate.now().plusMonths(1));

        // 2. Crear un cliente estándar
        ClienteEstandarDTO clienteEstandar = new ClienteEstandarDTO();
        clienteEstandar.setNombre("Juan");
        clienteEstandar.setApellido("Perez");
        clienteEstandar.setEmail("juan.perez@example.com");
        clienteEstandar.setCelular("1234567890");
        clienteEstandar.setFoto("foto.jpg");
        clienteEstandar.setEstado(EstadoCliente.ACTIVO.name());
        clienteEstandar.setCupon(cupon);

        System.out.println("cupon"+clienteEstandar.getCupon());

        ClienteEstandarDTO clienteGuardado = clienteService.agregarClienteEstandar(clienteEstandar);

        clienteService.eliminarLogicamente(clienteGuardado.getId());

        ClienteEstandarDTO clienteEliminado = (ClienteEstandarDTO) clienteService.buscarPorID(clienteGuardado.getId());
        assertNotNull(clienteEliminado);
        assertEquals(EstadoCliente.INACTIVO.name(), clienteEliminado.getEstado(), "El cliente debería estar inactivo.");
    }

    @Test
    public void testAgregarClientePremium() {
        ClientePremiumDTO clientePremiumDTO = new ClientePremiumDTO();
        clientePremiumDTO.setApellido("Martinez");
        clientePremiumDTO.setNombre("Maria");
        clientePremiumDTO.setCelular("6542342321");
        clientePremiumDTO.setCreated(LocalDateTime.now());
        clientePremiumDTO.setEmail("maria@hotmail.com");
        clientePremiumDTO.setFoto("456");
        clientePremiumDTO.setUpdated(null);
        clientePremiumDTO.setEstado(EstadoCliente.ACTIVO.name());
        clientePremiumDTO.setPorcentajeDescuento(20); // Descuento para cliente premium

        clientePremiumDTO=clienteService.agregarClientePremium(clientePremiumDTO);

        ClientePremiumDTO nuevoClientePremium = clienteService.getClientePremium(clientePremiumDTO.getId());
        assertNotNull(nuevoClientePremium);
        assertEquals("Maria", nuevoClientePremium.getNombre());
    }

    @Test
    public void testEditarClientePremium() {
        ClientePremiumDTO clientePremiumDTO = new ClientePremiumDTO();
        clientePremiumDTO.setApellido("Martinez");
        clientePremiumDTO.setNombre("Maria");
        clientePremiumDTO.setCelular("6543324221");
        clientePremiumDTO.setCreated(LocalDateTime.now());
        clientePremiumDTO.setEmail("mariaadwd@hotmail.com");
        clientePremiumDTO.setFoto("456");
        clientePremiumDTO.setUpdated(null);
        clientePremiumDTO.setEstado(EstadoCliente.ACTIVO.name());
        clientePremiumDTO.setPorcentajeDescuento(20);

        clientePremiumDTO=clienteService.agregarClientePremium(clientePremiumDTO);

        // Editar cliente premium
        ClientePremiumDTO clientePremiumEditado = new ClientePremiumDTO();
        clientePremiumEditado.setApellido("Martinez");
        clientePremiumEditado.setNombre("Ana");
        clientePremiumEditado.setCelular("654321");
        clientePremiumEditado.setCreated(clientePremiumDTO.getCreated());
        clientePremiumEditado.setEmail("mariaawfdf@hotmail.com");
        clientePremiumEditado.setFoto("456");
        clientePremiumEditado.setUpdated(LocalDateTime.now());
        clientePremiumEditado.setEstado(EstadoCliente.ACTIVO.name());
        clientePremiumEditado.setPorcentajeDescuento(20);

        clienteService.editarClientePremium(clientePremiumDTO.getId(), clientePremiumEditado);

        ClientePremiumDTO result = clienteService.getClientePremium(clientePremiumDTO.getId());
        assertEquals("Ana", result.getNombre());
    }


    @Test
    public void testAgregarClienteEstandar_EmailDuplicado() {
        // Crear cliente inicial
        ClienteEstandarDTO clienteEstandarDTO1 = new ClienteEstandarDTO();
        clienteEstandarDTO1.setApellido("Lopez");
        clienteEstandarDTO1.setNombre("Juan");
        clienteEstandarDTO1.setCelular("123456");
        clienteEstandarDTO1.setCreated(LocalDateTime.now());
        clienteEstandarDTO1.setEmail("juan123123@gmail.com");
        clienteEstandarDTO1.setFoto("foto_juan.jpg");
        clienteEstandarDTO1.setUpdated(null);
        clienteEstandarDTO1.setEstado(EstadoCliente.ACTIVO.name());

        // Agregar el primer cliente
        clienteService.agregarClienteEstandar(clienteEstandarDTO1);

        // Intentar agregar un cliente con el mismo email
        ClienteEstandarDTO clienteEstandarDTO2 = new ClienteEstandarDTO();
        clienteEstandarDTO2.setApellido("Gonzalez");
        clienteEstandarDTO2.setNombre("Pedro");
        clienteEstandarDTO2.setCelular("987654");
        clienteEstandarDTO2.setCreated(LocalDateTime.now());
        clienteEstandarDTO2.setEmail("juan123123@gmail.com"); // Mismo email que el primer cliente
        clienteEstandarDTO2.setFoto("foto_pedro.jpg");
        clienteEstandarDTO2.setUpdated(null);
        clienteEstandarDTO2.setEstado(EstadoCliente.ACTIVO.name());

        // Verificar que se lance la excepción
        assertThrows(EmailDuplicadoException.class, () -> {
            clienteService.agregarClienteEstandar(clienteEstandarDTO2);
        });
    }

    @Test
    public void testAgregarClienteEstandar_CelularDuplicado() {
        // Crear cliente inicial
        ClienteEstandarDTO clienteEstandarDTO1 = new ClienteEstandarDTO();
        clienteEstandarDTO1.setApellido("Lopez");
        clienteEstandarDTO1.setNombre("Juan");
        clienteEstandarDTO1.setCelular("123456789");
        clienteEstandarDTO1.setCreated(LocalDateTime.now());
        clienteEstandarDTO1.setEmail("juan@gmail.com");
        clienteEstandarDTO1.setFoto("foto_juan.jpg");
        clienteEstandarDTO1.setUpdated(null);
        clienteEstandarDTO1.setEstado(EstadoCliente.ACTIVO.name());

        // Agregar el primer cliente
        clienteService.agregarClienteEstandar(clienteEstandarDTO1);

        // Intentar agregar un cliente con el mismo celular
        ClienteEstandarDTO clienteEstandarDTO2 = new ClienteEstandarDTO();
        clienteEstandarDTO2.setApellido("Gonzalez");
        clienteEstandarDTO2.setNombre("Pedro");
        clienteEstandarDTO2.setCelular("123456789"); // Mismo celular que el primer cliente
        clienteEstandarDTO2.setCreated(LocalDateTime.now());
        clienteEstandarDTO2.setEmail("pedro@gmail.com");
        clienteEstandarDTO2.setFoto("foto_pedro.jpg");
        clienteEstandarDTO2.setUpdated(null);
        clienteEstandarDTO2.setEstado(EstadoCliente.ACTIVO.name());

        // Verificar que se lance la excepción
        assertThrows(CelularDuplicadoException.class, () -> {
            clienteService.agregarClienteEstandar(clienteEstandarDTO2);
        });
    }

    @Test
    public void testAgregarClientePremium_CelularDuplicado() {
        ClienteEstandarDTO clienteEstandarDTO1 = new ClienteEstandarDTO();
        clienteEstandarDTO1.setApellido("Lopez");
        clienteEstandarDTO1.setNombre("Juan");
        clienteEstandarDTO1.setCelular("987654321");
        clienteEstandarDTO1.setCreated(LocalDateTime.now());
        clienteEstandarDTO1.setEmail("juan@gmail.com");
        clienteEstandarDTO1.setFoto("foto_juan.jpg");
        clienteEstandarDTO1.setUpdated(null);
        clienteEstandarDTO1.setEstado(EstadoCliente.ACTIVO.name());

        // Agregar el primer cliente
        clienteService.agregarClienteEstandar(clienteEstandarDTO1);

        // Intentar agregar un cliente con el mismo celular
        ClientePremiumDTO clientePremiumDTO2 = new ClientePremiumDTO();
        clientePremiumDTO2.setApellido("Garcia");
        clientePremiumDTO2.setNombre("Luis");
        clientePremiumDTO2.setCelular("987654321"); // Mismo celular que el primer cliente
        clientePremiumDTO2.setCreated(LocalDateTime.now());
        clientePremiumDTO2.setEmail("luis@gmail.com");
        clientePremiumDTO2.setFoto("foto_luis.jpg");
        clientePremiumDTO2.setUpdated(null);
        clientePremiumDTO2.setEstado(EstadoCliente.ACTIVO.name());
        clientePremiumDTO2.setPorcentajeDescuento(50.0);

        // Verificar que se lance la excepción
        assertThrows(CelularDuplicadoException.class, () -> {
            clienteService.agregarClientePremium(clientePremiumDTO2);
        });
    }



}

