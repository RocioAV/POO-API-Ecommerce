package ar.edu.unju.fi.poo.tp8poo.mapper;

import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {


    Producto toProducto(ProductoDTO productoDTO);


    ProductoDTO toProductoDTO(Producto producto);


    List<ProductoDTO> toProductoDTOList (List<Producto> productos);

    List<Producto> toProductoList (List<ProductoDTO> productosDTO);
}
