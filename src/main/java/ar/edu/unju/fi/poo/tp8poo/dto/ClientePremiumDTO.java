package ar.edu.unju.fi.poo.tp8poo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClientePremiumDTO extends ClienteDTO implements Serializable{
	@Schema(description = "Porcentaje de descuento del cliente premium", example = "15.5")
	private Double porcentajeDescuento;
}
