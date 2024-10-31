package ar.edu.unju.fi.poo.tp8poo.service;

import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.mapper.ProductoMapper;
import ar.edu.unju.fi.poo.tp8poo.entity.Producto;
import ar.edu.unju.fi.poo.tp8poo.repository.ProductoRepository;
import ar.edu.unju.fi.poo.tp8poo.util.EstadoProducto;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
        log.info("Creando producto: Nombre={}", productoDTO.getNombre());
        if (productoDTO.getProveedor() == null) {
            log.error("El producto debe tener un Proveedor");
            throw new IllegalArgumentException("El producto debe tener un proveedor asignado.");
        }

        Producto producto = productoMapper.toProducto(productoDTO);
        Producto savedProducto = productoRepository.save(producto);
        log.info("Producto creado con éxito: ID={}, Nombre={}", savedProducto.getId(), savedProducto.getNombre());
        return productoMapper.toProductoDTO(savedProducto);
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
        log.info("Editando producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Producto no encontrado con ID: {}", id);
                    return new EntityNotFoundException("Producto no encontrado");
                });
        producto.setCodigo(productoDTO.getCodigo());
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setCantidad(productoDTO.getCantidad());
        producto.setImagen(productoDTO.getImagen());
        producto.setProveedor(productoMapper.toProducto(productoDTO).getProveedor());

        Producto updatedProducto = productoRepository.save(producto);
        log.info("Producto editado con éxito con id: {}", updatedProducto.getId());
        return productoMapper.toProductoDTO(updatedProducto);
    }

    /**
     * Elimina un producto por su ID.
     *
     * @param id El ID del producto a eliminar.
     */
    public void deleteProducto(Long id) {
        log.info("Eliminando producto con ID: {}", id);
        productoRepository.deleteById(id);
        log.info("Producto eliminado con éxito");
    }

    /**
     * Realiza un borrado lógico de un producto, cambiando su estado a false.
     *
     * @param id El ID del producto a eliminar lógicamente.
     * @throws EntityNotFoundException Si no se encuentra un producto con el ID proporcionado.
     */
    public void deleteProductoLogico(Long id) {
        log.info("Realizando borrado lógico de producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Producto no encontrado con ID: {}", id);
                    return new EntityNotFoundException("Producto no encontrado");
                });
        producto.setEstado(EstadoProducto.NO_DISPONIBLE.getEstado());
        productoRepository.save(producto);
        log.info("Borrado lógico realizado con éxito para el producto con ID: {}", id);
    }

    /**
     * Busca un producto por su ID.
     *
     * @param id El ID del producto a buscar.
     * @return El producto encontrado como un objeto DTO.
     * @throws EntityNotFoundException Si no se encuentra un producto con el ID proporcionado.
     */
    public ProductoDTO findById(Long id) {
        log.info("Buscando producto con ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Producto no encontrado con ID: {}", id);
                    return new EntityNotFoundException("Producto no encontrado");
                });
        log.info("Producto encontrado: ID={}, Nombre={}", producto.getId(), producto.getNombre());
        return productoMapper.toProductoDTO(producto);
    }

    /**
     * Obtiene todos los productos.
     *
     * @return Una lista de productos como objetos DTO.
     */
    public List<ProductoDTO> findAll() {
        log.info("Obteniendo todos los productos disponibles");

        List<ProductoDTO> productosDTO = productoMapper.toProductoDTOList(productoRepository.findByEstado(EstadoProducto.DISPONIBLE.getEstado()));
        log.info("Productos encontrados: {}", productosDTO.size());
        return productosDTO;

    }

    /**
     * Busca productos por código.
     *
     * @param codigo El código del producto a buscar.
     * @return El producto encontrado como un objeto DTO.
     */
    public ProductoDTO findByCodigo(String codigo) {
        log.info("Buscando producto por código: {}", codigo);
        Producto producto = productoRepository.findByCodigoContainingIgnoreCase(codigo);
        if (producto == null) {
            log.error("Producto no encontrado");
            throw new EntityNotFoundException("Producto no encontrado");
        }
        log.info("Producto encontrado: ID={}, Nombre={}, Codigo={}", producto.getId(), producto.getNombre(), producto.getCodigo());
        return productoMapper.toProductoDTO(producto);
    }

    /**
     * Busca productos por nombre.
     *
     * @param nombre El nombre del producto a buscar.
     * @return Una lista de productos encontrados como objetos DTO.
     */
    public List<ProductoDTO> findByNombre(String nombre) {
        log.info("Buscando productos por nombre: {}", nombre);
        List<Producto> productos = productoRepository.findByNombreContainingIgnoreCase(nombre);
        log.info("Productos encontrados: {}", productos.size());
        return productoMapper.toProductoDTOList(productos);
    }

    /**
     * Busca productos por descripción.
     *
     * @param descripcion La descripción del producto a buscar.
     * @return Una lista de productos encontrados como objetos DTO.
     */
    public List<ProductoDTO> findByDescripcion(String descripcion) {
        log.info("Buscando productos por descripción: {}", descripcion);
        List<Producto> productos = productoRepository.findByDescripcionContainingIgnoreCase(descripcion);
        if (productos.isEmpty()) {
            log.error("Producto no encontrado");
            throw new EntityNotFoundException("Producto no encontrado");
        }
        log.info("Productos encontrados: {}", productos.size());
        return productoMapper.toProductoDTOList(productos);
    }

    /**
     * Verifica si el producto con el ID especificado tiene stock disponible.
     *
     * @param id ID del producto a verificar.
     * @throws NegocioException si el producto no tiene stock disponible.
     */
    public void validarProductoSinStock(Long id){
        log.info("Validando stock del producto con ID {}", id);
        ProductoDTO producto= findById(id);
        if (producto.getCantidad()<=0){
            log.warn("El producto con ID {} no tiene stock", id);
            throw new NegocioException("El producto NO tiene stock");
        }
    }

    public void validarProductoDisponible(Long id){
        log.info("Validando stock del producto con ID {}", id);
        ProductoDTO producto= findById(id);
        if (producto.getEstado().equals(EstadoProducto.NO_DISPONIBLE.getEstado())){
            log.warn("El producto con ID {} no esta disponible", id);
            throw new NegocioException("El producto NO se encuentra disponible");
        }
    }

    /**
     * Descuenta una unidad del stock del producto. Si el stock llega a cero, actualiza el estado del producto.
     *
     * @param id  ID del producto a descontar del stock.
     */
    public void descontarStock(Long id){
        log.info("Descontando stock para el producto con ID {}", id);
        ProductoDTO producto= findById(id);
        producto.setCantidad(producto.getCantidad()-1);
        editProducto(producto.getId(), producto);
        if(producto.getCantidad()==0){
            deleteProductoLogico(producto.getId());
        }

    }

}
