package ar.edu.unju.fi.poo.tp8poo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import ar.edu.unju.fi.poo.tp8poo.dto.ProveedorDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Proveedor;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)

public interface ProveedorMapper {
    ProveedorDTO toProveedorDTO(Proveedor proveedor);

    Proveedor toProveedor(ProveedorDTO proveedorDTO);

    List<ProveedorDTO> toProveedorDTOList (List<Proveedor> proveedores);

    List<Proveedor> toProveedorList (List<ProveedorDTO> proveedoresDTO);
}