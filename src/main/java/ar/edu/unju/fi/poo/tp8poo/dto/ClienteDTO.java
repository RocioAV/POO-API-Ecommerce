package ar.edu.unju.fi.poo.tp8poo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "ID del cliente", example = "1")
	private Long id;

	@Schema(description = "Nombre del cliente", example = "Juan")
	private String nombre;

	@Schema(description = "Apellido del cliente", example = "Perez")
	private String apellido;

	@Schema(description = "Celular del cliente", example = "+54123456789")
	private String celular;

	@Schema(description = "Correo electrónico del cliente", example = "fabriz@gamil.com")
	private String email;

	@Schema(description = "Foto del cliente en formato base64")
	private String foto;

	@Schema(description = "Imagen del cliente como archivo")
	private MultipartFile imagen;

	@Schema(description = "Estado del cliente", example = "ACTIVO")
	private String estado="ACTIVO";
}