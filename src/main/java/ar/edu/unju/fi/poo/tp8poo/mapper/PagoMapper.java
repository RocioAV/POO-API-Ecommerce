package ar.edu.unju.fi.poo.tp8poo.mapper;


import ar.edu.unju.fi.poo.tp8poo.dto.*;
import ar.edu.unju.fi.poo.tp8poo.entity.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoMapper {
    // Métodos para objetos individuales
    PagoDebitoDTO toPagoDebitoDTO(PagoDebito pagoDebito);

    PagoTransferenciaDTO toPagoTransferenciaDTO(PagoTransferencia pagoTransferencia);

    PagoDebito toPagoDebitoEntity(PagoDebitoDTO pagoDebitoDTO);

    PagoTransferencia toPagoTransferenciaEntity(PagoTransferenciaDTO pagoTransferenciaDTO);

    List<PagoDebitoDTO> toPagoDebitoDTOList(List<PagoDebito> pagoDebitos);

    List<PagoTransferenciaDTO> toPagoTransferenciaDTOList(List<PagoTransferencia> pagoTransferencias);

    List<PagoDebito> toPagoDebitoEntityList(List<PagoDebitoDTO> pagoDebitoDTOs);

    List<PagoTransferencia> toPagoTransferenciaEntityList(List<PagoTransferenciaDTO> pagoTransferenciaDTOs);
    default Pago toPagoEntity(PagoDTO pagoDTO) {
        if (pagoDTO instanceof PagoTransferenciaDTO) {
            return toPagoTransferenciaEntity((PagoTransferenciaDTO) pagoDTO);
        } else if (pagoDTO instanceof PagoDebitoDTO) {
            return toPagoDebitoEntity((PagoDebitoDTO) pagoDTO);
        }
        throw new IllegalArgumentException("Tipo de pago no soportado: " + pagoDTO.getClass().getName());
    }
    default PagoDTO toPagoDTO(Pago pago) {
        if (pago instanceof PagoTransferencia) {
            return toPagoTransferenciaDTO((PagoTransferencia)pago);
        } else if (pago instanceof PagoDebito) {
            return toPagoDebitoDTO((PagoDebito)pago);
        }
        return null;
    }
}
