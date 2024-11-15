package ar.edu.unju.fi.poo.tp8poo.controller.interfaces;

import ar.edu.unju.fi.poo.tp8poo.dto.FiltroVentaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Tag(name = "Gestion de Venta", description = "Operaciones relacionadas a Venta, creacion y filtrado")

public interface IDocVentaResource {

    @Operation(
            summary = "Obtiene todas las ventas",
            description = "Devuelve una lista de todas las ventas registradas en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ventas obtenidas exitosamente", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error en el servidor al acceder a la base de datos", content = @Content)
            }
    )
    public ResponseEntity<Map<String, Object>> getAllVentas();

    //**********************************************************************

    @Operation(
            summary = "Crea una venta",
            description = "Crea una nueva venta registrando el producto y cliente asociados, así como la forma de pago." +
                    """
                            Recibe CUATRO parametros obligatorios
                            - ID del producto (Long/number)
                            - ID del cliente (Long/number)
                            - Forma de pago (CREDITO,DEBITO,TRANSFERENCIA) (String)
                            - Codigo del token (Numero de 6 digitos) (este se genera previamente, llegara al correo del cliente)
                            """,
            parameters = {
                    @Parameter(name = "idProducto",description = "ID del producto", required = true, example = "1"),
                    @Parameter(name = "idCliente",description = "ID del cliente", required = true, example = "1"),
                    @Parameter(name = "formaPago",description = "Forma de pago", required = true, example = "DEBITO"),
                    @Parameter(name = "tokenCodigo",description = "Codigo del token generado", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Venta creada con éxito", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    public ResponseEntity<Map<String, Object>> agregarVenta(@RequestParam Long idProducto,@RequestParam Long idCliente, @RequestParam String formaPago, @RequestParam String tokenCodigo);
    //**********************************************************************

    @Operation(
            summary = "Filtra ventas",
            description = """
                    Filtra las ventas según los criterios proporcionados en el filtro.
                    BODY
                    - nombreCliente (String)
                    - idCliente (Long)
                    - fechaInicio (String yyyy-MM-dd)
                    - fechaFin (String yyyy-MM-dd)
                    
                    Criterios a tener en cuenta:
                    - Elimina atributos por los cuales no se quiera buscar.
                    - La búsqueda simultánea por nombre y ID lanzará una excepción.
                    - Ambas fechas deben estar presentes si se desea buscar por rango de fecha.
                    - La fecha de inicio no debe ser posterior a la fecha fin.
                    
                    Combinaciones posibles:
                    - Nombre y rango de fechas
                    - ID y rango de fechas
                    - Solo rango de fechas
                    - Solo nombre
                    - Solo ID
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ventas filtradas obtenidas exitosamente", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Error en los criterios de filtro", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    public ResponseEntity<Map<String, Object>> filtrarVentas(@ModelAttribute FiltroVentaDTO filtroDTO);
    //**********************************************************************
    @Operation(
            summary = "Obtiene una venta por ID",
            description = "Devuelve los detalles de una venta específica a partir de su ID." +
                    """
                            Recibe un unico PARAMETRO
                            - ID de la venta (Long/number)
                            """,
            parameters = @Parameter(name = "id",description = "ID de venta (Long)", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Venta obtenida con éxito", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Venta no encontrada", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    public ResponseEntity<Map<String, Object>> getVentaById(@PathVariable("id") Long id);
    //**********************************************************************

}
