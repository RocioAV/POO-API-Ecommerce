package ar.edu.unju.fi.poo.tp8poo;
import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ProveedorDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.ProductoService;
import ar.edu.unju.fi.poo.tp8poo.service.ProveedorService;
import ar.edu.unju.fi.poo.tp8poo.testUtil.TestUtils;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoProducto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
 class TestProductoService {
    @Autowired
    ProductoService productoService;
    @Autowired
    ProveedorService proveedorService;


    static ProveedorDTO proveedorDTO;
    static ProductoDTO productoDTO;
    MultipartFile multipartFile;
    String workspacePath = System.getProperty("user.dir");
    String rutaArchivo1 = workspacePath + "/src/test/java/ar/edu/unju/fi/poo/tp8poo/img/parlante.webp";

    @BeforeEach
    public void setUp() {
        proveedorDTO = new ProveedorDTO(null, "Proveedor", "proveedor@gmail.com", "28853324", true);

        productoDTO = new ProductoDTO();
        productoDTO.setEstado(EstadoProducto.DISPONIBLE.getEstado());
       proveedorDTO= proveedorService.crearProveedor(proveedorDTO);
    }
    private void setUpProducto(String codigo, String nombre, String descripcion, Double precio, Integer cantidad,MultipartFile multipartFile) {
        productoDTO.setCodigo(codigo);
        productoDTO.setNombre(nombre);
        productoDTO.setDescripcion(descripcion);
        productoDTO.setPrecio(precio);
        productoDTO.setCantidad(cantidad);
        productoDTO.setFile(multipartFile);
        productoDTO.setIdProveedor(proveedorDTO.getId());
    }

    @Test
     void testCreateProductoCorrectoConImagem() throws IOException {
        multipartFile= TestUtils.generarMultipartFile(rutaArchivo1);
        setUpProducto("PROD001", "Producto 1", "Descripción del producto 1", 100.0, 10,multipartFile);
        ProductoDTO productoCreado = productoService.createProducto(productoDTO);
        assertEquals("PROD001", productoCreado.getCodigo());
    }

    @Test
     void testCreateProductoConProveedorInexistente() {
        setUpProducto("PROD002", "Producto 2", "Descripción del producto 2", 200.0, 5,null);
        productoDTO.setIdProveedor(8L);

        Exception exception = assertThrows(NegocioException.class, () -> productoService.createProducto(productoDTO));

        assertEquals("Proveedor no encontrado", exception.getMessage());
    }

    @Test
     void testDeleteLogicoProducto() {
        setUpProducto("PROD008","Producto 3","Descripción del producto 3",150.0,20,null);

        ProductoDTO createdProducto = productoService.createProducto(productoDTO);
        productoService.deleteProductoLogico(createdProducto.getId());

        ProductoDTO deletedProducto = productoService.findById(createdProducto.getId());
        assertEquals("No disponible", deletedProducto.getEstado());
    }

    @Test
     void testUpdateProducto() throws IOException {
        multipartFile= TestUtils.generarMultipartFile(rutaArchivo1);
        setUpProducto("PROD003","Producto 4","Descripción del producto 4",180.0,15,multipartFile);

        ProductoDTO createdProducto = productoService.createProducto(productoDTO);

        createdProducto.setNombre("Producto Modificado");
        createdProducto.setDescripcion("Descripción modificada");

        ProductoDTO updatedProducto = productoService.editProducto(createdProducto.getId(), createdProducto);

        assertEquals("Producto Modificado", updatedProducto.getNombre());
        assertEquals("Descripción modificada", updatedProducto.getDescripcion());
    }

    @Test
     void testFindByCodigo() {
        setUpProducto("PROD005","Producto 5","Descripción del producto 5",500.0,50,null);

        productoService.createProducto(productoDTO);

        ProductoDTO result = productoService.findByCodigo("PROD005");

        assertNotNull(result);
        assertEquals("PROD005", result.getCodigo());
    }

    @Test
     void testFindByNombre() {
        setUpProducto("PROD006","Producto 6","Descripción del producto 6",650.0,65,null);
        productoService.createProducto(productoDTO);
        List<ProductoDTO> result = productoService.findByNombre("Producto 6");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Producto 6", result.get(0).getNombre());
    }

    @Test
     void testFindByDescripcion() {
        setUpProducto("PROD007","Producto 7","Descripción del producto 7",700.0,70,null);

        productoService.createProducto(productoDTO);

        List<ProductoDTO> result = productoService.findByDescripcion("Descripción del producto 7");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Descripción del producto 7", result.get(0).getDescripcion());
    }
}
