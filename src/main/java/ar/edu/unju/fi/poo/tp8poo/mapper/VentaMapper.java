package ar.edu.unju.fi.poo.tp8poo.mapper;

import ar.edu.unju.fi.poo.tp8poo.dto.VentaDTO;
import ar.edu.unju.fi.poo.tp8poo.entity.Venta;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring", uses = {ClienteMapper.class, ProductoMapper.class, PagoMapper.class})
public interface VentaMapper {

    Venta toVentaEntity(VentaDTO ventaDTO);

    VentaDTO toVentaDTO(Venta venta);

    List<VentaDTO> toVentaDTOList (List<Venta> ventas);

    List<Venta> toVentaEntityList (List<VentaDTO> ventasDTO);


}
