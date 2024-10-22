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
	private Long id;
	private String nombre;
	private String apellido;
	private String email;
	private String celular;
	private String foto;
	private String estado;
	private String created;
	private String updated; 
	private double porcentajeDescuento;
}
