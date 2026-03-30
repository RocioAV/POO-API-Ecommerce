package ar.edu.unju.fi.poo.tp8poo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroVentaDTO {
    @Schema(description = "nombre del cliente", example = "fabri")
    private String nombreCliente;
    @Schema(description = "ID del cliente", example = "1")
    private Long idCliente;
    @Schema(description = "Fecha inicio de busqueda", example = "2024-10-10")
    private String fechaDesde;
    @Schema(description = "Fecha fin de busqueda", example = "2024-12-12")
    private String fechaHasta;
}
