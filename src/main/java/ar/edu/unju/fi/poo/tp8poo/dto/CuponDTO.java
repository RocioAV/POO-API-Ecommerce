package ar.edu.unju.fi.poo.tp8poo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CuponDTO {

    @Schema(description = "ID del cupón", example = "1")
    private Long id;

    @Schema(description = "Fecha de expiración del cupón", example = "2024-12-31")
    private String fechaExpiracion;

    @Schema(description = "Porcentaje de descuento del cupón", example = "10.0")
    private double porcentajeDescuento;
}
