package ar.edu.unju.fi.poo.tp8poo.mapper;

import ar.edu.unju.fi.poo.tp8poo.dto.CuponDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;
import ar.edu.unju.fi.poo.tp8poo.entity.Producto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CuponMapper {
    Cupon toCupon(CuponDTO cuponDTO);


    CuponDTO toCuponDTO(Cupon cupon);


    List<CuponDTO> toCuponesDTOList (List<Cupon> cupones);

    List<Cupon> toCuponesList (List<CuponDTO> cuponesDTO);
}
