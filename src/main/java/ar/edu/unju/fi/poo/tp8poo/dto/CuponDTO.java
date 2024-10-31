package ar.edu.unju.fi.poo.tp8poo.dto;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CuponDTO {

    private Long id;

    private String fechaExpiracion;

    private double porcentajeDescuento;
}
