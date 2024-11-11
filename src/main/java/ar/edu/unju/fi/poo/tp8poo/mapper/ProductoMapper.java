package ar.edu.unju.fi.poo.tp8poo.mapper;

import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Producto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @InheritInverseConfiguration
    Producto toProducto(ProductoDTO productoDTO);


    @Mapping(target = "idProveedor", source = "proveedor.id")
    ProductoDTO toProductoDTO(Producto producto);


    List<ProductoDTO> toProductoDTOList (List<Producto> productos);

}
