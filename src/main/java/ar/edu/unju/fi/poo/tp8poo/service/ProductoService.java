package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.mapper.ProductoMapper;
import ar.edu.unju.fi.poo.tp8poo.entity.Producto;
import ar.edu.unju.fi.poo.tp8poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoProducto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {
    @Autowired
    ProductoRepository productoRepository;

    @Autowired
    ProductoMapper productoMapper;


    /**
     * Crea un producto.
     *
     * @param productoDTO Objeto que contiene los datos del producto a crear o actualizar.
     * @return El producto creado o actualizado como un objeto DTO.
     * @throws IllegalArgumentException Si el proveedor del producto es nulo.
     */
    public ProductoDTO createProducto(ProductoDTO productoDTO) {
        if (productoDTO.getProveedor() == null) {
            throw new IllegalArgumentException("El producto debe tener un proveedor asignado.");
        }
        System.out.println(productoDTO.getProveedor());
        System.out.println(productoDTO);
        Producto producto = productoMapper.toProducto(productoDTO);
        System.out.println("Producto mapeado: " + producto);

        Producto savedProducto = productoRepository.save(producto);
        return productoMapper.toProductoDTO(savedProducto);
    }

    /**
     * Busca un producto por su ID.
     *
     * @param id El ID del producto a buscar.
     * @return El producto encontrado como un objeto DTO.
     * @throws EntityNotFoundException Si no se encuentra un producto con el ID proporcionado.
     */
    public ProductoDTO findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        return productoMapper.toProductoDTO(producto);
    }

    /**
     * Obtiene todos los productos.
     *
     * @return Una lista de productos como objetos DTO.
     */
    public List<ProductoDTO> findAll() {
        return productoMapper.toProductoDTOList(productoRepository.findByEstado(EstadoProducto.DISPONIBLE.getEstado()));
    }

    /**
     * Busca productos por código.
     *
     * @param codigo El código del producto a buscar.
     * @return El producto encontrado como un objeto DTO.
     */
    public ProductoDTO findByCodigo(String codigo) {
        Producto producto = productoRepository.findByCodigoContainingIgnoreCase(codigo);
        return productoMapper.toProductoDTO(producto);
    }

    /**
     * Busca productos por nombre.
     *
     * @param nombre El nombre del producto a buscar.
     * @return Una lista de productos encontrados como objetos DTO.
     */
    public List<ProductoDTO> findByNombre(String nombre) {
        List<Producto> productos = productoRepository.findByNombreContainingIgnoreCase(nombre);
        return productoMapper.toProductoDTOList(productos);
    }

    /**
     * Busca productos por descripción.
     *
     * @param descripcion La descripción del producto a buscar.
     * @return Una lista de productos encontrados como objetos DTO.
     */
    public List<ProductoDTO> findByDescripcion(String descripcion) {
        List<Producto> productos = productoRepository.findByDescripcionContainingIgnoreCase(descripcion);
        return productoMapper.toProductoDTOList(productos);
    }


    /**
     * Edita un producto existente.
     *
     * @param id          El ID del producto a editar.
     * @param productoDTO Objeto que contiene los nuevos datos del producto.
     * @return El producto editado como un objeto DTO.
     * @throws EntityNotFoundException Si no se encuentra un producto con el ID proporcionado.
     */
    public ProductoDTO editProducto(Long id, ProductoDTO productoDTO) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        producto.setCodigo(productoDTO.getCodigo());
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setCantidad(productoDTO.getCantidad());
        producto.setImagen(productoDTO.getImagen());
        producto.setProveedor(productoMapper.toProducto(productoDTO).getProveedor());

        Producto updatedProducto = productoRepository.save(producto);
        return productoMapper.toProductoDTO(updatedProducto);
    }

    /**
     * Elimina un producto por su ID.
     *
     * @param id El ID del producto a eliminar.
     */
    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }

    /**
     * Realiza un borrado lógico de un producto, cambiando su estado a false.
     *
     * @param id El ID del producto a eliminar lógicamente.
     * @throws EntityNotFoundException Si no se encuentra un producto con el ID proporcionado.
     */
    public void deleteProductoLogico(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));


        producto.setEstado(EstadoProducto.NO_DISPONIBLE.getEstado());
        productoRepository.save(producto);
    }

}
