package ar.edu.unju.fi.poo.tp8poo.entity;

import ar.edu.unju.fi.poo.tp8poo.util.EstadoProducto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = true)
    private String codigo;

    @Column
    private String nombre;

    @Column
    private String descripcion;

    @Column
    private Double precio;

    @Column
    private Integer cantidad;

    @Column
    private String imagen;

    @Column
    private String estado;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

}
