package ar.edu.unju.fi.poo.tp8poo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cupon {
	 	@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;
		@Column(name="FECHA_EXPIRACION",nullable = false)
		private LocalDate fechaExpiracion;
		@Column(name="POERCENTAJE_DESCUENTO",nullable = false)
		private double porcentajeDescuento;




}
