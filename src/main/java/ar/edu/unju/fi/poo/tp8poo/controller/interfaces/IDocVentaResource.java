package ar.edu.unju.fi.poo.tp8poo.controller.interfaces;

import ar.edu.unju.fi.poo.tp8poo.dto.FiltroVentaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
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
    @Operation(
    	    summary = "Exporta las ventas filtradas",
    	    description = """
    	    		Este endpoint permite exportar una lista de ventas filtrada en diferentes formatos (PDF, Excel)
    	    		o devolver la lista filtrada directamente en formato JSON si no se especifica el formato.
    	    		
                    Body
                    - nombreCliente (String)
                    - idCliente (Long)
                    - fechaInicio (String yyyy-MM-dd)
                    - fechaFin (String yyyy-MM-dd)
                    
                    Criterios a tener en cuenta:
                    - Elimina atributos por los cuales no se quiera buscar.
                    - La búsqueda simultánea por nombre y ID se priorizara el ID.
                    - Ambas fechas deben estar presentes si se desea buscar por rango de fecha.
                    - La fecha de inicio no debe ser posterior a la fecha fin.
                    
                    Combinaciones posibles:
                    - Nombre y rango de fechas
                    - ID y rango de fechas
                    - Solo rango de fechas
                    - Solo nombre
                    - Solo ID
                    
    	    		Comportamiento:
    	    		- Si el parámetro formato no se proporciona o está vacío, el endpoint devolverá la lista de ventas filtrada en formato `JSON`.
    	    		- Si se especifica un formato válido (`pdf` o `excel`), el endpoint generará el archivo correspondiente y lo devolverá.

    	    		Parámetros:
    	    		- **filtroDTO**: Objeto que contiene los criterios de filtrado para las ventas.
    	    		- **formato**: Opcional. Define el formato de exportación. Puede ser:
    	    		  - `pdf`: Exporta las ventas en formato PDF.
    	    		  - `excel`: Exporta las ventas en formato Excel.
    	    		  - `ambos`: Exporta las ventas en ambos formatos comprimidos en un archivo Zip.

    	    		Respuestas posibles:
    	    		- Retorna una lista de ventas en JSON si no se especifica un formato.
    	    		- Retorna un archivo descargable en el formato especificado si el formato es válido.
    	        """,
    	    responses = {
    	        @ApiResponse(
    	            responseCode = "200", 
    	            description = "Ventas exportadas con éxito.", 
    	            content = @Content
    	        ),
    	        @ApiResponse(
    	            responseCode = "400", 
    	            description = "Error en los parámetros de solicitud o en los criterios de filtro.", 
    	            content = @Content
    	        ),
    	        @ApiResponse(
    	            responseCode = "500", 
    	            description = "Error interno del servidor.", 
    	            content = @Content
    	        )
    	    }
    	)
	public ResponseEntity<Object> exportar(FiltroVentaDTO filtroDTO, String formato);

}
