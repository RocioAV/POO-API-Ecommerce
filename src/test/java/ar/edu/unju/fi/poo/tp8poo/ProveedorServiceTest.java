package ar.edu.unju.fi.poo.tp8poo;

import ar.edu.unju.fi.poo.tp8poo.dto.ProveedorDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Proveedor;
import ar.edu.unju.fi.poo.tp8poo.mapper.ProveedorMapper;
import ar.edu.unju.fi.poo.tp8poo.repository.ProveedorRepository;
import ar.edu.unju.fi.poo.tp8poo.service.ProveedorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private ProveedorMapper proveedorMapper;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedor;
    private ProveedorDTO proveedorDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor 1");
        proveedor.setEmail("proveedor1@example.com");
        proveedor.setTelefono("123456789");

        proveedorDTO = new ProveedorDTO();
        proveedorDTO.setId(1L);
        proveedorDTO.setNombre("Proveedor 1");
        proveedorDTO.setEmail("proveedor1@example.com");
        proveedorDTO.setTelefono("123456789");
    }

    @Test
    void testCrearProveedor() {
        when(proveedorMapper.toProveedor(any())).thenReturn(proveedor);
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);
        when(proveedorMapper.toProveedorDTO(any())).thenReturn(proveedorDTO);

        ProveedorDTO proveedorCreado = proveedorService.crearProveedor(proveedorDTO);

        assertNotNull(proveedorCreado);
        assertEquals("Proveedor 1", proveedorCreado.getNombre());

        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    void testObtenerProveedores() {
        when(proveedorRepository.findAll()).thenReturn(Collections.singletonList(proveedor));
        when(proveedorMapper.toProveedorDTOList(any())).thenReturn(Collections.singletonList(proveedorDTO));

        List<ProveedorDTO> proveedores = proveedorService.obtenerProveedores();

        assertNotNull(proveedores);
        assertTrue(proveedores.size() > 0);

        verify(proveedorRepository, times(1)).findAll();
    }

    @Test
    void testObtenerProveedorPorId() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedorMapper.toProveedorDTO(any())).thenReturn(proveedorDTO);

        ProveedorDTO proveedorObtenido = proveedorService.obtenerProveedorPorId(1L);

        assertNotNull(proveedorObtenido);
        assertEquals("Proveedor 1", proveedorObtenido.getNombre());

        verify(proveedorRepository, times(1)).findById(1L);
    }

    @Test
    void testActualizarProveedor() {
        ProveedorDTO proveedorActualizadoDTO = new ProveedorDTO();
        proveedorActualizadoDTO.setNombre("Proveedor 1 Actualizado");
        proveedorActualizadoDTO.setEmail("actualizado@example.com");
        proveedorActualizadoDTO.setTelefono("987654321");

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(invocation -> {
            Proveedor prov = invocation.getArgument(0);
            prov.setNombre(proveedorActualizadoDTO.getNombre());
            prov.setEmail(proveedorActualizadoDTO.getEmail());
            prov.setTelefono(proveedorActualizadoDTO.getTelefono());
            return prov;
        });
        when(proveedorMapper.toProveedorDTO(any())).thenAnswer(invocation -> {
            Proveedor prov = invocation.getArgument(0);
            ProveedorDTO dto = new ProveedorDTO();
            dto.setId(prov.getId());
            dto.setNombre(prov.getNombre());
            dto.setEmail(prov.getEmail());
            dto.setTelefono(prov.getTelefono());
            return dto;
        });

        ProveedorDTO proveedorActualizado = proveedorService.actualizarProveedor(1L, proveedorActualizadoDTO);

        assertNotNull(proveedorActualizado);
        assertEquals("Proveedor 1 Actualizado", proveedorActualizado.getNombre());
        assertEquals("actualizado@example.com", proveedorActualizado.getEmail());
        assertEquals("987654321", proveedorActualizado.getTelefono());

        verify(proveedorRepository, times(1)).findById(1L);
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }


    @Test
    void testEliminarProveedor() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));

        proveedorService.eliminarProveedor(1L);

        verify(proveedorRepository, times(1)).findById(1L);
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }
}
