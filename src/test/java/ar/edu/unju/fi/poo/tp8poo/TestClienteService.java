package ar.edu.unju.fi.poo.tp8poo;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.CuponDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.CelularDuplicadoException;
import ar.edu.unju.fi.poo.tp8poo.exceptions.EmailDuplicadoException;
import ar.edu.unju.fi.poo.tp8poo.service.ClienteService;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class TestClienteService {

    @Autowired
    private ClienteService clienteService; // Clase bajo prueba

    private ClienteEstandarDTO clienteEstandarDTO;
    private ClientePremiumDTO clientePremiumDTO;

    @BeforeEach
    public void setUp() {
        // Inicializar un cliente estándar
        clienteEstandarDTO = new ClienteEstandarDTO();
        clienteEstandarDTO.setApellido("Lopez");
        clienteEstandarDTO.setNombre("Raul");
        clienteEstandarDTO.setCelular("1234561341");
        clienteEstandarDTO.setCupon(new CuponDTO(null, "2024-12-02", 10.0));
        clienteEstandarDTO.setEmail("raul5@hotmail.com");
        clienteEstandarDTO.setFoto("https://drive.google.com/uc?id=1SYGQFHAOJmU60I2V-zCsefMtam0tkTjg");
        clienteEstandarDTO.setEstado(EstadoCliente.ACTIVO.name());

        // Inicializar un cliente premium
        clientePremiumDTO = new ClientePremiumDTO();
        clientePremiumDTO.setApellido("Martinez");
        clientePremiumDTO.setNombre("Maria");
        clientePremiumDTO.setCelular("6542342321");
        clientePremiumDTO.setEmail("maria@hotmail.com");
        clientePremiumDTO.setFoto("https://drive.google.com/uc?id=1Mvv0XIqmdgTg3_qG0-jurVnifKHrMiLz");
        clientePremiumDTO.setEstado(EstadoCliente.ACTIVO.name());
        clientePremiumDTO.setPorcentajeDescuento(20.0); // Descuento para cliente premium
    }

    @Test
    public void testAgregarClienteEstandar() {
        ClienteEstandarDTO nuevoCliente = clienteService.agregarClienteEstandar(clienteEstandarDTO);
        assertNotNull(nuevoCliente);
        assertEquals("Raul", nuevoCliente.getNombre());
    }

    @Test
    public void testEditarClienteEstandar() {
        ClienteEstandarDTO nuevoCliente =  clienteService.agregarClienteEstandar(clienteEstandarDTO); // Guardar el cliente inicial

        // Editar cliente
        ClienteEstandarDTO clienteEstandarEditado = new ClienteEstandarDTO();
        clienteEstandarEditado.setApellido("Lopez");
        clienteEstandarEditado.setNombre("Daniel");
        clienteEstandarEditado.setCelular("1233123456");
        clienteEstandarEditado.setEmail("raul@hotmail.com");
        clienteEstandarEditado.setFoto(clienteEstandarDTO.getFoto());
        clienteEstandarEditado.setEstado(EstadoCliente.ACTIVO.name());
        clienteEstandarEditado.setCupon(clienteEstandarDTO.getCupon());

        clienteService.editarClienteEstandar(nuevoCliente.getId(), clienteEstandarEditado);

        ClienteEstandarDTO result = clienteService.getClienteEstandar(nuevoCliente.getId());
        assertEquals("Daniel", result.getNombre());
    }

    @Test
    public void testEliminarLogicamenteClienteEstandar() {
        ClienteEstandarDTO nuevoCliente =  clienteService.agregarClienteEstandar(clienteEstandarDTO);

        clienteService.eliminarLogicamente(nuevoCliente.getId());

        ClienteEstandarDTO clienteEliminado = (ClienteEstandarDTO) clienteService.buscarPorID(nuevoCliente.getId());
        assertNotNull(clienteEliminado);
        assertEquals(EstadoCliente.INACTIVO.name(), clienteEliminado.getEstado(), "El cliente debería estar inactivo.");
    }

    @Test
    public void testAgregarClientePremium() {
        clientePremiumDTO = clienteService.agregarClientePremium(clientePremiumDTO);

        ClientePremiumDTO nuevoClientePremium = clienteService.getClientePremium(clientePremiumDTO.getId());
        assertNotNull(nuevoClientePremium);
        assertEquals("Maria", nuevoClientePremium.getNombre());
    }

    @Test
    public void testEditarClientePremium() {
        clientePremiumDTO = clienteService.agregarClientePremium(clientePremiumDTO);

        // Editar cliente premium
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
    }


    @Test
    public void testAgregarClienteEstandar_EmailDuplicado() {
        ClienteEstandarDTO nuevoClienteEstandar =  clienteService.agregarClienteEstandar(clienteEstandarDTO);

        // Intentar agregar un cliente con el mismo email
        ClienteEstandarDTO clienteEstandarDTO2 = new ClienteEstandarDTO();
        clienteEstandarDTO2.setApellido("Gonzalez");
        clienteEstandarDTO2.setNombre("Pedro");
        clienteEstandarDTO2.setCelular("987654");
        clienteEstandarDTO2.setEmail("raul5@hotmail.com"); // Mismo email que el primer cliente
        clienteEstandarDTO2.setFoto("https://drive.google.com/uc?id=1Mvv0XIqmdgTg3_qG0-jurVnifKHrMiLz");
        clienteEstandarDTO2.setEstado(EstadoCliente.ACTIVO.name());

        assertThrows(EmailDuplicadoException.class, () -> {
            clienteService.agregarClienteEstandar(clienteEstandarDTO2);
        });
    }

    @Test
    public void testAgregarClienteEstandar_CelularDuplicado() {
        ClienteEstandarDTO nuevoClienteEstandar =  clienteService.agregarClienteEstandar(clienteEstandarDTO);

        ClienteEstandarDTO clienteEstandarDTO2 = new ClienteEstandarDTO();
        clienteEstandarDTO2.setApellido("Gonzalez");
        clienteEstandarDTO2.setNombre("Pedro");
        clienteEstandarDTO2.setCelular("1234561341"); // Mismo celular que el primer cliente
        clienteEstandarDTO2.setEmail("pedro@gmail.com");
        clienteEstandarDTO2.setFoto("https://drive.google.com/uc?id=1nB1VhKuCFmO6jhiAqiPmUWZo1Iwgm8Fy");
        clienteEstandarDTO2.setEstado(EstadoCliente.ACTIVO.name());

        assertThrows(CelularDuplicadoException.class, () -> {
            clienteService.agregarClienteEstandar(clienteEstandarDTO2);
        });
    }

    @Test
    public void testAgregarClientePremium_CelularDuplicado() {
        ClienteEstandarDTO nuevoClienteEstandar =  clienteService.agregarClienteEstandar(clienteEstandarDTO);

        ClientePremiumDTO clientePremiumDTO2 = new ClientePremiumDTO();
        clientePremiumDTO2.setApellido("Garcia");
        clientePremiumDTO2.setNombre("Luis");
        clientePremiumDTO2.setCelular("1234561341"); // Mismo celular que el primer cliente
        clientePremiumDTO2.setEmail("luis@gmail.com");
        clientePremiumDTO2.setFoto("https://drive.google.com/uc?id=1nB1VhKuCFmO6jhiAqiPmUWZo1Iwgm8Fy");
        clientePremiumDTO2.setEstado(EstadoCliente.ACTIVO.name());
        clientePremiumDTO2.setPorcentajeDescuento(50.0);

        assertThrows(CelularDuplicadoException.class, () -> {
            clienteService.agregarClientePremium(clientePremiumDTO2);
        });
    }



}

