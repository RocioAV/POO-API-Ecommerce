package ar.edu.unju.fi.poo.tp8poo;
import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ProveedorDTO;
import ar.edu.unju.fi.poo.tp8poo.service.ProductoService;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoProducto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class TestProductoService {
    @Autowired
    ProductoService productoService;


    @Test
    public void testCreateProductoCorrecto() {
        ProveedorDTO proveedorDTO = new ProveedorDTO(null,"Pepsi","pepsi@gmail.com","28853324",true);

        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setCodigo("PROD001");
        productoDTO.setNombre("Producto 1");
        productoDTO.setDescripcion("Descripción del producto 1");
        productoDTO.setPrecio(100.0);
        productoDTO.setCantidad(10);
        productoDTO.setEstado(EstadoProducto.DISPONIBLE.getEstado());
        productoDTO.setProveedor(proveedorDTO);

        ProductoDTO productoCreado=productoService.createProducto(productoDTO);
        assertEquals("PROD001",productoCreado.getCodigo());
    }

    @Test
    public void testCreateProductoSinProveedor() {
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setCodigo("PROD002");
        productoDTO.setNombre("Producto 2");
        productoDTO.setDescripcion("Descripción del producto 2");
        productoDTO.setPrecio(200.0);
        productoDTO.setCantidad(5);
        productoDTO.setEstado(EstadoProducto.DISPONIBLE.getEstado());
        productoDTO.setProveedor(null);  // Proveedor nulo


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.createProducto(productoDTO);
        });

        assertEquals("El producto debe tener un proveedor asignado.", exception.getMessage());
    }

    @Test
    public void testDeleteLogicoProducto() {
        ProveedorDTO proveedorDTO = new ProveedorDTO(null, "Coca","coca@gmail.com","28853324",true);

        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setCodigo("PROD003");
        productoDTO.setNombre("Producto 3");
        productoDTO.setDescripcion("Descripción del producto 3");
        productoDTO.setPrecio(150.0);
        productoDTO.setCantidad(20);
        productoDTO.setEstado(EstadoProducto.DISPONIBLE.getEstado());
        productoDTO.setProveedor(proveedorDTO);

        ProductoDTO createdProducto = productoService.createProducto(productoDTO);

        productoService.deleteProductoLogico(createdProducto.getId());

        ProductoDTO deletedProducto = productoService.findById(createdProducto.getId());
        assertEquals("No disponible", deletedProducto.getEstado());
    }

    @Test
    public void testUpdateProducto() {
        ProveedorDTO proveedorDTO = new ProveedorDTO(null, "Lays","Lays@gmail.com","28853324",true);
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setNombre("Producto 4");
        productoDTO.setDescripcion("Descripción del producto 4");
        productoDTO.setPrecio(180.0);
        productoDTO.setCantidad(15);
        productoDTO.setProveedor(proveedorDTO);

        ProductoDTO createdProducto = productoService.createProducto(productoDTO);

        createdProducto.setNombre("Producto Modificado");
        createdProducto.setDescripcion("Descripción modificada");

        ProductoDTO updatedProducto = productoService.editProducto(createdProducto.getId(), createdProducto);

        assertEquals("Producto Modificado", updatedProducto.getNombre());
        assertEquals("Descripción modificada", updatedProducto.getDescripcion());
    }


    @Test
    public void testFindByCodigo() {
        ProveedorDTO proveedorDTO = new ProveedorDTO(null, "Pepsi", "pepsi@gmail.com", "28853324",true);

        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setCodigo("PROD005");
        productoDTO.setNombre("Producto 5");
        productoDTO.setDescripcion("Descripción del producto 5");
        productoDTO.setPrecio(500.0);
        productoDTO.setCantidad(50);
        productoDTO.setEstado(EstadoProducto.DISPONIBLE.getEstado());
        productoDTO.setProveedor(proveedorDTO);

        productoService.createProducto(productoDTO);

        ProductoDTO result = productoService.findByCodigo("PROD005");

        assertNotNull(result);
        assertEquals("PROD005", result.getCodigo());
    }

    @Test
    public void testFindByNombre() {
        ProveedorDTO proveedorDTO = new ProveedorDTO(null, "Coca-Cola", "coca@gmail.com", "12345678",true);

        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setCodigo("PROD006");
        productoDTO.setNombre("Producto 6");
        productoDTO.setDescripcion("Descripción del producto 6");
        productoDTO.setPrecio(650.0);
        productoDTO.setCantidad(65);
        productoDTO.setEstado(EstadoProducto.DISPONIBLE.getEstado());
        productoDTO.setProveedor(proveedorDTO);

        productoService.createProducto(productoDTO);

        List<ProductoDTO> result = productoService.findByNombre("Producto 6");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Producto 6", result.get(0).getNombre());
    }

    @Test
    public void testFindByDescripcion() {
        ProveedorDTO proveedorDTO = new ProveedorDTO(null, "Sprite", "sprite@gmail.com", "87654321",true);

        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setCodigo("PROD007");
        productoDTO.setNombre("Producto 7");
        productoDTO.setDescripcion("Descripción del producto 7");
        productoDTO.setPrecio(700.0);
        productoDTO.setCantidad(70);
        productoDTO.setEstado(EstadoProducto.DISPONIBLE.getEstado());
        productoDTO.setProveedor(proveedorDTO);

        productoService.createProducto(productoDTO);


        List<ProductoDTO> result = productoService.findByDescripcion("Descripción del producto 7");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Descripción del producto 7", result.get(0).getDescripcion());
    }
}
