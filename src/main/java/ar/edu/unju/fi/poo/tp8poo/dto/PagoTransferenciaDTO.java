package ar.edu.unju.fi.poo.tp8poo.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoTransferenciaDTO extends PagoDTO {

    private String cuil;

    private String nroTransferencia;

    private String EntidadBancariaOrigen;

    private String cbuDestino;
}
