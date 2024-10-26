package ar.edu.unju.fi.poo.tp8poo.dto;

import ar.edu.unju.fi.poo.tp8poo.entity.Pago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {
    private Integer id;

    private LocalDateTime fechaYHora;

    private ClienteDTO cliente;

    private ProductoDTO producto;

    private Double precioProducto;

    private Pago formaPago;
}
