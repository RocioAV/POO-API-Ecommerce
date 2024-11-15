package ar.edu.unju.fi.poo.tp8poo.exceptions;

import ar.edu.unju.fi.poo.tp8poo.util.ConstantesMensajes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones cuando falta un parámetro requerido en una solicitud HTTP.
     * Esta excepción es lanzada cuando un parámetro obligatorio en una solicitud no está presente.
     *
     * @param ex La excepción lanzada (MissingServletRequestParameterException).
     * @return Una respuesta HTTP con código de estado 400 (Bad Request) y un mensaje detallado
     *         sobre el parámetro faltante.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, String> response = new HashMap<>();
        response.put(ConstantesMensajes.MENSAJE, "Falta uno o más parámetros requeridos.");
        response.put(ConstantesMensajes.ERROR, ex.getParameterName() + " es obligatorio.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja las excepciones cuando hay un desajuste de tipo de argumento en una solicitud.
     * Esta excepción se lanza cuando un argumento de un parámetro en el controlador no coincide
     * con el tipo esperado.
     *
     * @param ex La excepción lanzada (MethodArgumentTypeMismatchException).
     * @return Una respuesta HTTP con código de estado 400 (Bad Request) y un mensaje detallado
     *         sobre el tipo incorrecto de argumento y el tipo esperado.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put(ConstantesMensajes.ERROR, "Tipo de argumento incorrecto");
        response.put(ConstantesMensajes.MENSAJE,
                String.format("El argumento '%s' debe ser de tipo '%s'", ex.getName(), Objects.requireNonNull(
                        ex.getRequiredType()).getSimpleName()));
        response.put("detalle", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    /**
     * Maneja las excepciones de entrada/salida (IOException) que pueden ocurrir en procesos
     * de lectura o escritura de datos, como la conversión de moneda.
     *
     * @param ex La excepción lanzada (IOException).
     * @return Una respuesta HTTP con código de estado 500 (Internal Server Error) y un mensaje
     *         detallado sobre el error de entrada/salida ocurrido.
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(IOException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put(ConstantesMensajes.MENSAJE, "Error al convertir moneda");
        response.put(ConstantesMensajes.ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Maneja las excepciones de conversión de tipo de objeto (ClassCastException) cuando ocurre
     * un error al intentar convertir un objeto a un tipo incompatible.
     *
     * @param ex La excepción lanzada (ClassCastException).
     * @return Una respuesta HTTP con código de estado 400 (Bad Request) y un mensaje detallado
     *         sobre el error de conversión ocurrido.
     */
    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<Map<String, Object>> handleClassCastException(ClassCastException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put(ConstantesMensajes.MENSAJE, "Ocurrió un error de conversión");
        response.put(ConstantesMensajes.ERROR,"El ID proporcionado pertenece a un cliente de tipo 'Premium', pero se intentó buscar como 'Estándar' o viceversa. Verifique que el tipo de cliente coincida con el tipo esperado.");
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}