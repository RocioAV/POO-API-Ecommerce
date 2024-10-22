package ar.edu.unju.fi.poo.tp8poo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientePremiumDTO extends ClienteDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double porcentajeDescuento;
}
