package ar.edu.unju.fi.poo.tp8poo.util;

import lombok.Getter;

@Getter
public enum EstadoProducto {
    DISPONIBLE("Disponible"),
    NO_DISPONIBLE("No disponible");
    private final String estado;
    EstadoProducto(String estado) {
        this.estado = estado;
    }
}
