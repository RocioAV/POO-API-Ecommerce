package ar.edu.unju.fi.poo.tp8poo.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("ESTANDAR")
public class ClienteEstandar extends Cliente {

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "CUPON_ID", referencedColumnName = "ID", unique = true)
	private Cupon cupon;
}
