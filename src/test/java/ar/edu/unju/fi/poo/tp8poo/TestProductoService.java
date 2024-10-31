package ar.edu.unju.fi.poo.tp8poo;
import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ProveedorDTO;
import ar.edu.unju.fi.poo.tp8poo.service.ProductoService;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoProducto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
 class TestProductoService {
    @Autowired
    ProductoService productoService;

    // Inicializa variables para las pruebas
    static ProveedorDTO proveedorDTO;
    static ProductoDTO productoDTO;

    @BeforeEach
    public void setUp() {
        proveedorDTO = new ProveedorDTO(null, "Proveedor", "proveedor@gmail.com", "28853324", true);

        productoDTO = new ProductoDTO();
        productoDTO.setEstado(EstadoProducto.DISPONIBLE.getEstado());
        productoDTO.setProveedor(proveedorDTO);
    }
    private void setUpProducto(String codigo, String nombre, String descripcion, Double precio, Integer cantidad,String imagen) {
        productoDTO.setCodigo(codigo);
        productoDTO.setNombre(nombre);
        productoDTO.setDescripcion(descripcion);
        productoDTO.setPrecio(precio);
        productoDTO.setCantidad(cantidad);
        productoDTO.setImagen(imagen);
    }

    @Test
     void testCreateProductoCorrecto() {
        setUpProducto("PROD001", "Producto 1", "Descripción del producto 1", 100.0, 10,"https://drive.google.com/uc?id=1BFiAyGd6NKHNgU83uu2sGJHM2sT-o5vJ");
        ProductoDTO productoCreado = productoService.createProducto(productoDTO);
        assertEquals("PROD001", productoCreado.getCodigo());
    }

    @Test
     void testCreateProductoSinProveedor() {
        setUpProducto("PROD002", "Producto 2", "Descripción del producto 2", 200.0, 5,"https://drive.google.com/uc?id=1BFiAyGd6NKHNgU83uu2sGJHM2sT-o5vJ");
        productoDTO.setProveedor(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> productoService.createProducto(productoDTO));

        assertEquals("El producto debe tener un proveedor asignado.", exception.getMessage());
    }

    @Test
     void testDeleteLogicoProducto() {
        setUpProducto("PROD008","Producto 3","Descripción del producto 3",150.0,20,"https://drive.google.com/uc?id=1tde1iwN_TST5XqBz5u4L1IwX8hDvWq5d");

        ProductoDTO createdProducto = productoService.createProducto(productoDTO);
        productoService.deleteProductoLogico(createdProducto.getId());

        ProductoDTO deletedProducto = productoService.findById(createdProducto.getId());
        assertEquals("No disponible", deletedProducto.getEstado());
    }

    @Test
     void testUpdateProducto() {
        setUpProducto("PROD003","Producto 4","Descripción del producto 4",180.0,15,"https://drive.google.com/uc?id=1BFiAyGd6NKHNgU83uu2sGJHM2sT-o5vJ");

        ProductoDTO createdProducto = productoService.createProducto(productoDTO);

        createdProducto.setNombre("Producto Modificado");
        createdProducto.setDescripcion("Descripción modificada");

        ProductoDTO updatedProducto = productoService.editProducto(createdProducto.getId(), createdProducto);

        assertEquals("Producto Modificado", updatedProducto.getNombre());
        assertEquals("Descripción modificada", updatedProducto.getDescripcion());
    }

    @Test
     void testFindByCodigo() {
        setUpProducto("PROD005","Producto 5","Descripción del producto 5",500.0,50,"https://drive.google.com/uc?id=15Ygz6H2wh9YZ-rXtpDVBFirUteKMTQzV");

        productoService.createProducto(productoDTO);

        ProductoDTO result = productoService.findByCodigo("PROD005");

        assertNotNull(result);
        assertEquals("PROD005", result.getCodigo());
    }

    @Test
     void testFindByNombre() {
        setUpProducto("PROD006","Producto 6","Descripción del producto 6",650.0,65,"https://drive.google.com/uc?id=1rdFfrURbr-AwXGBuME7i9zYlhanOuImu");
        productoService.createProducto(productoDTO);
        List<ProductoDTO> result = productoService.findByNombre("Producto 6");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Producto 6", result.get(0).getNombre());
    }

    @Test
     void testFindByDescripcion() {
        setUpProducto("PROD007","Producto 7","Descripción del producto 7",700.0,70,"https://drive.google.com/uc?id=1zlt_-cVGKJE5RFw9q0oIgGlT1yN__2vw");

        productoService.createProducto(productoDTO);

        List<ProductoDTO> result = productoService.findByDescripcion("Descripción del producto 7");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Descripción del producto 7", result.get(0).getDescripcion());
    }
}
