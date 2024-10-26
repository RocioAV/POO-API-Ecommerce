package ar.edu.unju.fi.poo.tp8poo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PagoTranferencia extends Pago {

    @Column(nullable = false)
    private String cuil;
    @Column(nullable = false)
    private String nroTransferencia;
    @Column(nullable = false)
    private String EntidadBancariaOrigen;
    @Column(nullable = false)
    private String cbuDestino;
}
