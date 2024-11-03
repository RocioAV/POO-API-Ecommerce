package ar.edu.unju.fi.poo.tp8poo.entity;

import ar.edu.unju.fi.poo.tp8poo.util.EstadoCliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
/*Esta anotación pertenece a JPA/Hibernate y define la estrategia que se utilizará para la herencia de entidades*/
@DiscriminatorColumn(name = "tipo_cliente", discriminatorType = DiscriminatorType.STRING)
/*Se usa junto con la estrategia de herencia SINGLE_TABLE para definir una columna que identificará el
 * tipo de cada registro (subclase) almacenado en la tabla.*/
public abstract class Cliente {
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	 	@Column(length = 10 )
	    private String nombre;

	 	@Column(length= 10 )
	    private String apellido;

	 	@Column(unique=true,  length= 50)
	    private String email;

	 	@Column(unique=true,  length= 15)
	    private String celular;

	 	@Column
	    private String foto;

	 	@Column
	    @Enumerated(EnumType.STRING)
	    private EstadoCliente estado;

		@CreationTimestamp
		private LocalDateTime created;
		@UpdateTimestamp
		private LocalDateTime updated;




	    

	    
}
