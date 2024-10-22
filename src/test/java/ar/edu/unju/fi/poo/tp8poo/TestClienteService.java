package ar.edu.unju.fi.poo.tp8poo;

import ar.edu.unju.fi.poo.tp8poo.dto.ClienteEstandarDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ClientePremiumDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.ClienteEstandar;
import ar.edu.unju.fi.poo.tp8poo.entity.ClientePremium;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ClienteExistenteException;
import ar.edu.unju.fi.poo.tp8poo.exceptions.ClienteInexixtenteExcepcion;
import ar.edu.unju.fi.poo.tp8poo.repository.ClienteRepository;
import ar.edu.unju.fi.poo.tp8poo.service.ClienteService;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest

class TestClienteService {
    @Mock
    private ClienteRepository clienteRepository; // Mock del repositorio

    @InjectMocks
    private ClienteService clienteService; // Clase bajo prueba

    private ClienteEstandarDTO clienteEstandarDTO;
    private ClienteEstandarDTO clienteEstandarDTOEditado;
    private ClienteEstandarDTO clienteEstandarDTOPruebaEdicion;
    private ClienteEstandar clienteEstandar;
    private ClienteEstandar clienteEstandarPruebaEdicion;
    private ClientePremiumDTO clientePremiumDTO;
    private ClientePremium clientePremium;
    private ClientePremium clientePremiumPrueba;
    private ClientePremiumDTO clienteDTOPremiumPrueba;
    private ClientePremiumDTO clientePremiumDTOEditado;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        clienteEstandar = new ClienteEstandar();
        clienteEstandar.setNombre("Juan");
        clienteEstandar.setApellido("Perez");
        clienteEstandar.setCelular("123456");
        clienteEstandar.setCupon(new Cupon(1L,LocalDate.of(2024, 12, 2),15));
        clienteEstandar.setEmail("juan@hotmail.com");
        clienteEstandar.setEstado(EstadoCliente.ACTIVO);
        clienteEstandar.setId(1L);
        clienteEstandar.setUpdated(null);
        clienteEstandar.setCreated(LocalDateTime.now());
        clienteEstandar.setFoto("123");
        
        clientePremiumPrueba=new ClientePremium();
        clientePremiumPrueba.setId(5L);
        clientePremiumPrueba.setNombre("Ricardo");
        clientePremiumPrueba.setApellido("Fort");
        clientePremiumPrueba.setCelular("123456");
        clientePremiumPrueba.setEmail("elrichard@hotmail.com");
        clientePremiumPrueba.setPorcentajeDescuento(25);
        clientePremiumPrueba.setEstado(EstadoCliente.ACTIVO);
        clientePremiumPrueba.setCreated(LocalDateTime.now());
        clientePremiumPrueba.setFoto("122");
        clientePremiumPrueba.setUpdated(null);
        
        clienteDTOPremiumPrueba= new ClientePremiumDTO();
        clienteDTOPremiumPrueba.setId(5L);
        clienteDTOPremiumPrueba.setNombre("Ricardo");
        clienteDTOPremiumPrueba.setApellido("Fort");
        clienteDTOPremiumPrueba.setCelular("123456");
        clienteDTOPremiumPrueba.setEmail("elrichard@hotmail.com");
        clienteDTOPremiumPrueba.setPorcentajeDescuento(25);
        clienteDTOPremiumPrueba.setEstado(clientePremiumPrueba.getEstado().name());
        clienteDTOPremiumPrueba.setCreated(clientePremiumPrueba.getCreated().format(formatter));
        clienteDTOPremiumPrueba.setFoto("122");
        clienteDTOPremiumPrueba.setUpdated(null);
        
        
        clientePremium = new ClientePremium();
        clientePremium.setNombre("Alberto");
        clientePremium.setApellido("Lopez");
        clientePremium.setCelular("123457");
        clientePremium.setEmail("alber@hotmail.com");
        clientePremium.setPorcentajeDescuento(12);
        clientePremium.setEstado(EstadoCliente.ACTIVO);
        clientePremium.setCreated(LocalDateTime.now());
        clientePremium.setUpdated(null);
        clientePremium.setId(3L);
        clientePremium.setFoto("123");
        
        clienteEstandarDTO = new ClienteEstandarDTO();
        clienteEstandarDTO.setApellido("Perez");
        clienteEstandarDTO.setNombre("Juan");
        clienteEstandarDTO.setCelular("123456");
        clienteEstandarDTO.setCupon(1L);
        clienteEstandarDTO.setPorcentajeDescuento(15);
        clienteEstandarDTO.setId(1L);
        clienteEstandarDTO.setCreated(clienteEstandar.getCreated().format(formatter));
        clienteEstandarDTO.setEmail("juan@hotmail.com");
        clienteEstandarDTO.setFoto("123");
        clienteEstandarDTO.setUpdated(null);
        clienteEstandarDTO.setEstado(clienteEstandar.getEstado().name());
        
        clientePremiumDTO = new ClientePremiumDTO();
        clientePremiumDTO.setNombre("Alberto");
        clientePremiumDTO.setApellido("Lopez");
        clientePremiumDTO.setCelular("123457");
        clientePremiumDTO.setEmail("alber@hotmail.com");
        clientePremiumDTO.setPorcentajeDescuento(12);
        clientePremiumDTO.setEstado(clientePremium.getEstado().name());
        clientePremiumDTO.setCreated(clientePremium.getCreated().format(formatter));
        clientePremiumDTO.setUpdated(null);
        clientePremiumDTO.setId(3L);
        clientePremiumDTO.setFoto("123");
        
        clientePremiumDTOEditado= new ClientePremiumDTO();
        clientePremiumDTOEditado.setNombre("Roberto");
        clientePremiumDTOEditado.setApellido("Lopez");
        clientePremiumDTOEditado.setCelular("123457");
        clientePremiumDTOEditado.setEmail("alber@hotmail.com");
        clientePremiumDTOEditado.setPorcentajeDescuento(12);
        clientePremiumDTOEditado.setEstado(clientePremium.getEstado().name());
        clientePremiumDTOEditado.setCreated(clientePremium.getCreated().format(formatter));
        clientePremiumDTOEditado.setUpdated(null);
        clientePremiumDTOEditado.setId(3L);
        clientePremiumDTOEditado.setFoto("123");
        
        clienteEstandarDTOEditado=new ClienteEstandarDTO();
        clienteEstandarDTOEditado.setApellido("Perez");
        clienteEstandarDTOEditado.setNombre("Daniel");
        clienteEstandarDTOEditado.setCelular("123459");
        clienteEstandarDTOEditado.setCupon(1L);
        clienteEstandarDTOEditado.setPorcentajeDescuento(15);
        clienteEstandarDTOEditado.setId(1L);
        clienteEstandarDTOEditado.setCreated(clienteEstandar.getCreated().format(formatter));
        clienteEstandarDTOEditado.setEmail("juan@hotmail.com");
        clienteEstandarDTOEditado.setFoto("123");
        clienteEstandarDTOEditado.setUpdated(null);
        clienteEstandarDTOEditado.setEstado(clienteEstandar.getEstado().name());
        
        clienteEstandarPruebaEdicion=new ClienteEstandar();
        clienteEstandarPruebaEdicion.setNombre("Raul");
        clienteEstandarPruebaEdicion.setApellido("Lopez");
        clienteEstandarPruebaEdicion.setCupon(new Cupon(3L,LocalDate.of(2024, 12, 2),10));
        clienteEstandarPruebaEdicion.setId(4L);
        clienteEstandarPruebaEdicion.setCelular("123456");
        clienteEstandarPruebaEdicion.setEmail("raul@hotmail.com");
        clienteEstandarPruebaEdicion.setCreated(LocalDateTime.now());
        clienteEstandarPruebaEdicion.setEstado(EstadoCliente.ACTIVO);
        clienteEstandarPruebaEdicion.setUpdated(null);
        clienteEstandarPruebaEdicion.setFoto("1234");
        
        
        clienteEstandarDTOPruebaEdicion=new ClienteEstandarDTO();
        clienteEstandarDTOPruebaEdicion.setApellido("Lopez");
        clienteEstandarDTOPruebaEdicion.setNombre("Raul");
        clienteEstandarDTOPruebaEdicion.setCelular("123456");
        clienteEstandarDTOPruebaEdicion.setCupon(3L);
        clienteEstandarDTOPruebaEdicion.setPorcentajeDescuento(10);
        clienteEstandarDTOPruebaEdicion.setId(4L);
        clienteEstandarDTOPruebaEdicion.setCreated(clienteEstandar.getCreated().format(formatter));
        clienteEstandarDTOPruebaEdicion.setEmail("raul@hotmail.com");
        clienteEstandarDTOPruebaEdicion.setFoto("123");
        clienteEstandarDTOPruebaEdicion.setUpdated(null);
        clienteEstandarDTOPruebaEdicion.setEstado(clienteEstandar.getEstado().name());
    }
    

    @Test
    public void testAgregarClienteEstandar() {
        when(clienteRepository.findByEmail(clienteEstandarDTO.getEmail())).thenReturn(null);
        when(clienteRepository.findByEmail(clienteEstandarDTO.getCelular())).thenReturn(null);

        clienteService.agregarClienteEstandar(clienteEstandarDTO);

        verify(clienteRepository, times(1)).save(any(ClienteEstandar.class));
    }

    @Test
    public void testGetClienteEstandar() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEstandar));

        ClienteEstandarDTO result = clienteService.getClienteEstandar(1L);

        assert result != null;
        assert result.getNombre().equals("Juan");
    }

    @Test
    public void testEditarClienteEstandar() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEstandar));

        ClienteEstandarDTO result = clienteService.editarClienteEstandar(1L, clienteEstandarDTOEditado);

        assert result.getNombre().equals("Daniel");
        verify(clienteRepository, times(1)).save(any(ClienteEstandar.class));
    }

    @Test
    public void testEliminarClienteEstandar() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEstandar));

        clienteService.eliminarClienteEstandar(1L);

        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testAgregarClientePremium() {
        when(clienteRepository.findByEmail(clientePremiumDTO.getEmail())).thenReturn(null);
        when(clienteRepository.findByEmail(clientePremiumDTO.getCelular())).thenReturn(null);

        clienteService.agregarClientePremium(clientePremiumDTO);

        verify(clienteRepository, times(1)).save(any(ClientePremium.class));
    }

    @Test
    public void testEditarClientePremium() {
        when(clienteRepository.findById(3L)).thenReturn(Optional.of(clientePremium));

        ClientePremiumDTO result = clienteService.editarClientePremium(3L, clientePremiumDTOEditado);

        assert result.getNombre().equals("Roberto");
        verify(clienteRepository, times(1)).save(any(ClientePremium.class));
    }

    @Test
    public void testEliminarClientePremium() {
        when(clienteRepository.findById(3L)).thenReturn(Optional.of(clientePremium));

        clienteService.eliminarClientePremium(3L);

        verify(clienteRepository, times(1)).deleteById(3L);
    }
    
    @Test 
    public void testAgregarEmailRepetido() {
    	 when(clienteRepository.findByEmail(clienteEstandarDTO.getEmail())).thenReturn(clienteEstandar);
    	 
    	 Exception exception = assertThrows(ClienteExistenteException.class, () -> {
    		 clienteService.agregarClienteEstandar(clienteEstandarDTO);;
         });

    	 assertEquals("El cliente con dicho correo ya existe",exception.getMessage());
    	 
    }

    @Test
    public void testAgregarTelefonoRepetido() {
        // Dado que el DTO de cliente estándar contiene un número de celular
        String celularRepetido = clienteEstandarDTO.getCelular();

        // Cuando se simula que el clienteRepository encuentra un cliente existente por el celular
        when(clienteRepository.findByCelular(celularRepetido)).thenReturn(new ClienteEstandar());

        // Entonces se espera que se lance la excepción ClienteExistenteException
        ClienteExistenteException exception = assertThrows(ClienteExistenteException.class, () -> {
            clienteService.agregarClienteEstandar(clienteEstandarDTO);
        });

        // Y se verifica que el mensaje de la excepción es el esperado
        assertEquals("El cliente con dicho número de celular ya existe", exception.getMessage());
    }
    
    @Test
    public void testEditarNroRepetidoEstandar() {
    	when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEstandar));
    	
    	 when(clienteRepository.findByCelular(clienteEstandarDTOPruebaEdicion.getCelular())).thenReturn(clienteEstandarPruebaEdicion);
    	
    	Exception exception = assertThrows(ClienteExistenteException.class, () -> {
      		 clienteService.editarClienteEstandar(1L, clienteEstandarDTOPruebaEdicion);
           });
        assertEquals("El cliente con dicho número de celular ya existe",exception.getMessage());
        verify(clienteRepository, never()).save(any(ClienteEstandar.class));
    }
    
    
    @Test
    public void nroRepetidoEstandarPremium() {
    	 when(clienteRepository.findByCelular(clienteEstandarDTO.getCelular())).thenReturn(clienteEstandar);
       	 
       	 Exception exception = assertThrows(ClienteExistenteException.class, () -> {
       		 clienteService.agregarClientePremium(clienteDTOPremiumPrueba);
            });

       	 assertEquals("El cliente con dicho número de celular ya existe",exception.getMessage());
    }
    
    @Test 
    public void eliminarInexistente() {
    	when(clienteRepository.findById(8L)).thenReturn(Optional.empty());
    	Exception exception= assertThrows(ClienteInexixtenteExcepcion.class,
    			 () -> {clienteService.eliminarClienteEstandar(8L);});
    	 assertEquals("Cliente no encontrado con ID: " + 8, exception.getMessage());
    	 verify(clienteRepository,never()).deleteById(8L);
    }
    
    @Test
    public void editarInexistente() {
    	when(clienteRepository.findById(8L)).thenReturn(Optional.empty());
    	Exception exception= assertThrows(ClienteInexixtenteExcepcion.class,
    			()->{
    				clienteService.editarClientePremium(8L, clienteDTOPremiumPrueba);
    			});
    	assertEquals("Cliente no encontrado con ID: " + 8,exception.getMessage());
    }
    
}

