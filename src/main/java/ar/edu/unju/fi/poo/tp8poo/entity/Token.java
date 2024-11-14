package ar.edu.unju.fi.poo.tp8poo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String valor;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;


    @Column(nullable = false)
    private Long clienteId;

    public Token(String valor, LocalDateTime fechaExpiracion, Long cliente) {
        this.valor = valor;
        this.fechaExpiracion = fechaExpiracion;
        this.clienteId = cliente;
    }

    public Token() {

    }
}
