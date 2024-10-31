package ar.edu.unju.fi.poo.tp8poo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {
    private Long id;

    private String fechaYHora;

    private ClienteDTO cliente;

    private ProductoDTO producto;

    private Double precioProducto;

    private String formaPago;
}
