package ar.edu.unju.fi.poo.tp8poo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("PREMIUM")
public class ClientePremium extends Cliente{

	@Column
	private Double porcentajeDescuento;
}
