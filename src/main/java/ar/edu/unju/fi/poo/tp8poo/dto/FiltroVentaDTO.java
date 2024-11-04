package ar.edu.unju.fi.poo.tp8poo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroVentaDTO {
    private String nombreCliente;
    private Long idCliente;
    private String fechaDesde;
    private String fechaHasta;
}
