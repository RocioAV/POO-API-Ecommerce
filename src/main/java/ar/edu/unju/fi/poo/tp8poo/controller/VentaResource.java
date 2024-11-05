package ar.edu.unju.fi.poo.tp8poo.controller;

import ar.edu.unju.fi.poo.tp8poo.dto.FiltroVentaDTO;
import ar.edu.unju.fi.poo.tp8poo.dto.VentaDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1/venta")
@Tag(name = "Gestion de Venta", description = "Operaciones relacionadas a Venta, creacion y filtrado")
public class VentaResource {

    private final VentaService ventaService;

    public VentaResource(VentaService ventaService) {
        this.ventaService = ventaService;
    }


    @GetMapping("/list")
    @Operation(
            summary = "Obtiene todas las ventas",
            description = "Devuelve una lista de todas las ventas registradas en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ventas obtenidas exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "No se encontraron ventas", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error en el servidor al acceder a la base de datos", content = @Content)
            }
    )
    public ResponseEntity<?> getAllVentas(){
        log.info("/api/v1/venta/list");
        Map<String, Object> response = new HashMap<>();
        try {
            List<VentaDTO> ventas = ventaService.findAll();
            if (ventas.isEmpty()) {
                log.info("No se encontraron ventas");
                response.put("message", "No se encontraron ventas");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("ventas", ventas);
            response.put("mensaje", "Ventas obtenidas con éxito");
            return ResponseEntity.ok(response);
        }catch (DataAccessException e) {
            log.error("Error de acceso a la base de datos al obtener el producto por ID", e);
            response.put("mensaje", "Error de base de datos al obtener el producto");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/create")
    @Operation(
            summary = "Crea una venta",
            description = "Crea una nueva venta registrando el producto y cliente asociados, así como la forma de pago.",
            parameters = {
                    @Parameter(name = "idProducto",description = "ID del producto", required = true, example = "1"),
                    @Parameter(name = "idCliente",description = "ID del cliente", required = true, example = "1"),
                    @Parameter(name = "formaPago",description = "Forma de  (CREDITO,DEBITO,TRANSFERENCIA)", required = true, example = "DEBITO")
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Venta creada con éxito", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    public ResponseEntity<?> agregarVenta(@RequestParam Long idProducto,
                                          @RequestParam Long idCliente,
                                          @RequestParam String formaPago) {
        log.info("/api/v1/venta/create");
        Map<String, Object> response = new HashMap<>();
        try{
            response.put("venta", ventaService.crearVenta(idProducto,idCliente,formaPago));
            response.put("mensaje", "Venta creada con exito");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (NegocioException e) {
            log.error("Problemas al registrar la venta");
            response.put("mensaje", "Error al crear la venta");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IOException e) {
            log.error("Problemas al registrar la venta");
            response.put("mensaje", "Error al convertir moneda");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            log.error("Proceso de creacion interrumpido");
            response.put("mensaje", "Error interno del servidor");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/filter")
    @Operation(
            summary = "Filtra ventas",
            description = """
                    Filtra las ventas según los criterios proporcionados en el filtro.
                    
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
                    @ApiResponse(responseCode = "200", description = "Ventas filtradas obtenidas exitosamente", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "204", description = "No se encontraron ventas que coincidan con el filtro", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Error en los criterios de filtro", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    public ResponseEntity<?> filtrarVentas(@ModelAttribute FiltroVentaDTO filtroDTO){
        log.info("/api/v1/venta/filter");
        Map<String, Object> response = new HashMap<>();
        try{
            List<VentaDTO> ventasFiltradas= ventaService.filtrarVentas(filtroDTO);
            if(ventasFiltradas.isEmpty()){
                log.info("No se encontraron ventas que concuerden con el filtro");
                return ResponseEntity.noContent().build();
            }
            response.put("ventas", ventasFiltradas);
            response.put("mensaje","Ventas de acuerdo al filtro obtenidas con exito ");
            return ResponseEntity.ok(response);

        }catch (NegocioException e) {
            response.put("mensaje", "Error al aplicar filtros");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al consultar el hsitoria de ventas");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/get/{id}")
    @Operation(
            summary = "Obtiene una venta por ID",
            description = "Devuelve los detalles de una venta específica a partir de su ID.",
            parameters = @Parameter(name = "id",description = "ID de venta", required = true, example = "1"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Venta obtenida con éxito", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "Venta no encontrada", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    public ResponseEntity<?> getVentaById(@PathVariable("id") Long id){
        log.info("/api/v1/venta/get/{id}");
        Map<String, Object> response = new HashMap<>();
        try{
            VentaDTO venta= ventaService.findById(id);
            if(venta==null){
                log.info("No se encontraron venta que con el id "+id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("venta", venta);
            response.put("mensaje","Venta encontrada con exito");
            return ResponseEntity.ok(response);

        }catch (NegocioException e) {
            response.put("mensaje", "Error al buscar la venta");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al buscar la venta");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
