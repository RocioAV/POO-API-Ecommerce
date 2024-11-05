package ar.edu.unju.fi.poo.tp8poo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ProveedorDTO {

	@Schema(description = "Identificador único del proveedor")
    private Long id;

    @Schema(description = "Nombre del proveedor", example = "Proveedor S.A.")
    private String nombre;

    @Schema(description = "Correo electrónico del proveedor", example = "proveedor@email.com")
    private String email;

    @Schema(description = "Número de teléfono del proveedor", example = "+541234567890")
    private String telefono;

    @Schema(description = "Estado del proveedor, activo o inactivo")
    private boolean estado;

}