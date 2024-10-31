package ar.edu.unju.fi.poo.tp8poo.entity;

import ar.edu.unju.fi.poo.tp8poo.util.FormaPago;
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
    private Long id;

    @Column
    private LocalDateTime fechaYHora;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="cliente_id", nullable=false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="producto_id", nullable=false)
    private Producto producto;

    @Column
    private Double precioProducto;

    @Column
    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;


}
