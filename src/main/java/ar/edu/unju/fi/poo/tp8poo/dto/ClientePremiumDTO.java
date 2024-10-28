package ar.edu.unju.fi.poo.tp8poo.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClientePremiumDTO extends ClienteDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Double porcentajeDescuento;
}
