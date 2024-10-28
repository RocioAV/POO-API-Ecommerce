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
public class PagoDebitoDTO extends PagoDTO {

    private String nombreTitular;

    private String apellidoTitular;

    private String nroTarjeta;

    private Integer mesVencimiento;

    private Integer anioVencimiento;

    private Integer codigoSeguridad;
}
