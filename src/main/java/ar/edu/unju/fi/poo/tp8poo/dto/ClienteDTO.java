package ar.edu.unju.fi.poo.tp8poo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO implements Serializable {
	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private Long id;
	    private String nombre;
	    private String apellido;
	    private String celular;
	    private String email;
	    private String foto;
	    private String estado; // Convertido de Enum a String
		private LocalDateTime created;
		private LocalDateTime updated;
}
