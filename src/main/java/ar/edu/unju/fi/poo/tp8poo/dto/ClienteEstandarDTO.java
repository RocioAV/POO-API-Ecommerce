package ar.edu.unju.fi.poo.tp8poo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClienteEstandarDTO extends ClienteDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "Cupon de descuento asignado al cliente estándar")
	private CuponDTO cupon;
}
