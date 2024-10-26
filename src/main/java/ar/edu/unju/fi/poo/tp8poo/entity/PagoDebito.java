package ar.edu.unju.fi.poo.tp8poo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PagoDebito extends Pago {

    @Column
    private String nombreTitular;
    @Column
    private String apellidoTitular;
    @Column(nullable = false)
    private String nroTarjeta;
    @Column(nullable = false)
    private Integer mesVencimiento;
    @Column(nullable = false)
    private Integer anioVencimiento;

}
