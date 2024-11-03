package ar.edu.unju.fi.poo.tp8poo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Falta uno o más parámetros requeridos.");
        response.put("error", ex.getParameterName() + " es obligatorio.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
