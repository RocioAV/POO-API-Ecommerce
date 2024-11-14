package ar.edu.unju.fi.poo.tp8poo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@ToString
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("ESTANDAR")
public class ClienteEstandar extends Cliente {

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CUPON_ID", referencedColumnName = "Id", unique = true)
	private Cupon cupon;

	@Override
	public double verificarDescuento() {
		return cupon == null || cuponVencido() ? 0 : this.cupon.getPorcentajeDescuento();
	}

	public boolean cuponVencido() {
		return cupon.getFechaExpiracion().isBefore(LocalDate.now());
	}
	public void expirarCupon(){
		if (verificarDescuento()!= 0){
			cupon.setFechaExpiracion(LocalDate.now().minusDays(1));
		}
	}

}
