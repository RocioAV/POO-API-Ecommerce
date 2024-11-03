package ar.edu.unju.fi.poo.tp8poo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@ToString
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cupon {

	 	@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@Column(name="FECHA_EXPIRACION")
		private LocalDate fechaExpiracion;

		@Column(name="PORCENTAJE_DESCUENTO")
		private double porcentajeDescuento;




}
