package ar.edu.unju.fi.poo.tp8poo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    @Schema(description = "ID del Producto", example = "1")
    private Long id;
    @Schema(description = "Codido del Producto", example = "PROD001")
    private String codigo;
    @Schema(description = "Nombre del Producto", example = "Parlante HP")
    private String nombre;
    @Schema(description = "Descripcion del Producto", example = "Bueno sonido,medidas 30x30")
    private String descripcion;
    @Schema(description = "Precio del Producto", example = "100.0")
    private Double precio;
    @Schema(description = "Stock disponible del Producto", example = "5")
    private Integer cantidad;

    private String imagen;

    @Schema(description = "Estado del producto", example = "Disponible")
    private String estado;
    @Schema(description = "ID del proveedor", example = "1")
    private Long idProveedor;

}
