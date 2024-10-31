package ar.edu.unju.fi.poo.tp8poo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CuponDTO {

    private Long id;

    private String fechaExpiracion;

    private double porcentajeDescuento;
}
