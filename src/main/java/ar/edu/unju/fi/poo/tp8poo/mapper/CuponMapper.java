package ar.edu.unju.fi.poo.tp8poo.mapper;

import ar.edu.unju.fi.poo.tp8poo.dto.CuponDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CuponMapper {
    @Mapping(source = "fechaExpiracion", target = "fechaExpiracion", dateFormat = "yyyy-MM-dd")
    Cupon toCuponEntity(CuponDTO cuponDTO);


    CuponDTO toCuponDTO(Cupon cupon);

}
