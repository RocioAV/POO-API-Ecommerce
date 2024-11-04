package ar.edu.unju.fi.poo.tp8poo.dto;


import lombok.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer cantidad;
    private String imagen;
    private MultipartFile file;
    private String estado;
    private Long idProveedor;

}
