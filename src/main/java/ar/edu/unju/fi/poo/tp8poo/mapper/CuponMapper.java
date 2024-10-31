package ar.edu.unju.fi.poo.tp8poo.mapper;

import ar.edu.unju.fi.poo.tp8poo.dto.CuponDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CuponMapper {
    @Mappings({
            @Mapping(source = "fechaExpiracion", target = "fechaExpiracion", dateFormat = "yyyy-MM-dd")
    })
    Cupon toCuponEntity(CuponDTO cuponDTO);


    CuponDTO toCuponDTO(Cupon cupon);


    List<CuponDTO> toCuponesDTOList (List<Cupon> cupones);

    List<Cupon> toCuponesList (List<CuponDTO> cuponesDTO);
}
