package ar.edu.unju.fi.poo.tp8poo;

import ar.edu.unju.fi.poo.tp8poo.dto.ProveedorDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Proveedor;
import ar.edu.unju.fi.poo.tp8poo.mapper.ProveedorMapper;
import ar.edu.unju.fi.poo.tp8poo.repository.ProveedorRepository;
import ar.edu.unju.fi.poo.tp8poo.service.ProveedorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProveedorServiceTest {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProveedorMapper proveedorMapper;

    @Autowired
    private ProveedorService proveedorService;

    private Proveedor proveedor;
    private ProveedorDTO proveedorDTO;

    @BeforeEach
    void setup() {
        proveedor = new Proveedor();
        proveedor.setNombre("Proveedor 1");
        proveedor.setEmail("proveedor1@email.com");
        proveedor.setTelefono("123456789");

        proveedorDTO = new ProveedorDTO();
        proveedorDTO.setNombre("Proveedor 1");
        proveedorDTO.setEmail("proveedor1@email.com");
        proveedorDTO.setTelefono("123456789");
    }

    @Test
    void testCrearProveedor() {
        ProveedorDTO proveedorCreado = proveedorService.crearProveedor(proveedorDTO);
        assertNotNull(proveedorCreado);
        assertEquals("Proveedor 1", proveedorCreado.getNombre());
        Proveedor proveedorGuardado = proveedorRepository.findById(proveedorCreado.getId()).orElse(null);
        assertNotNull(proveedorGuardado);
    }

    @Test
    void testObtenerProveedores() {
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        List<ProveedorDTO> proveedores = proveedorService.obtenerProveedores();
        assertNotNull(proveedores);
        assertFalse(proveedores.isEmpty());
        assertEquals(proveedorGuardado.getNombre(), proveedores.get(0).getNombre());
    }

    @Test
    void testObtenerProveedorPorId() {
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        ProveedorDTO proveedorObtenido = proveedorService.obtenerProveedorPorId(proveedorGuardado.getId());
        assertNotNull(proveedorObtenido);
        assertEquals("Proveedor 1", proveedorObtenido.getNombre());
    }

    @Test
    void testActualizarProveedor() {
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);

        ProveedorDTO proveedorActualizadoDTO = new ProveedorDTO();
        proveedorActualizadoDTO.setNombre("Proveedor 1 Actualizado");
        proveedorActualizadoDTO.setEmail("actualizado@email.com");
        proveedorActualizadoDTO.setTelefono("987654321");

        ProveedorDTO proveedorActualizado = proveedorService.actualizarProveedor(proveedorGuardado.getId(), proveedorActualizadoDTO);

        assertNotNull(proveedorActualizado);
        assertEquals("Proveedor 1 Actualizado", proveedorActualizado.getNombre());
        assertEquals("actualizado@email.com", proveedorActualizado.getEmail());
        assertEquals("987654321", proveedorActualizado.getTelefono());
    }

    @Test
    void testEliminarProveedor() {
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        proveedorService.eliminarProveedor(proveedorGuardado.getId());
        Proveedor proveedorEliminado = proveedorRepository.findById(proveedorGuardado.getId()).orElse(null);
        assertNotNull(proveedorEliminado);  
        assertFalse(proveedorEliminado.isEstado());  
    }
}
