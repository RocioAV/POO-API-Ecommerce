package ar.edu.unju.fi.poo.tp8poo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Venta {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private LocalDateTime fechaYHora;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="cliente_id", nullable=false)
    private Cliente cliente;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="producto_id", nullable=false)
    private Producto producto;

    private Double precioProducto;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="pago_id")
    private Pago formaPago;


}
