package ar.edu.unju.fi.poo.tp8poo.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ProveedorDTO {

    private Long id;

    private String nombre;

    private String email;

    private String telefono;

    private boolean estado;

}