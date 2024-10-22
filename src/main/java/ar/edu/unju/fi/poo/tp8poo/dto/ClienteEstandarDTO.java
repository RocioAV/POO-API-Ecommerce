package ar.edu.unju.fi.poo.tp8poo.dto;

import ar.edu.unju.fi.poo.tp8poo.entity.Cupon;
import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEstandarDTO extends ClienteDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Cupon cupon;

}
