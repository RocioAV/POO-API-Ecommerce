package ar.edu.unju.fi.poo.tp8poo.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String MENSAJE="mensaje";
    private static final String ERROR="error";

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, String> response = new HashMap<>();
        response.put(MENSAJE, "Falta uno o más parámetros requeridos.");
        response.put(ERROR, ex.getParameterName() + " es obligatorio.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleDataAccessException(DataAccessException ex) {
        Map<String, String> response = new HashMap<>();
        response.put(MENSAJE, "Error de base de datos ");
        response.put(ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put(ERROR, "Tipo de argumento incorrecto");
        response.put(MENSAJE,
                String.format("El argumento '%s' debe ser de tipo '%s'", ex.getName(), Objects.requireNonNull(
                        ex.getRequiredType()).getSimpleName()));
        response.put("detalle", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);


    }
}