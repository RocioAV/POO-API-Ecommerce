package ar.edu.unju.fi.poo.tp8poo.controller;

import ar.edu.unju.fi.poo.tp8poo.controller.interfaces.IDocProductoResource;
import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.exceptions.NegocioException;
import ar.edu.unju.fi.poo.tp8poo.service.ProductoService;
import ar.edu.unju.fi.poo.tp8poo.util.ConstantesMensajes;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/producto")
@Slf4j

public class ProductoResource implements IDocProductoResource {
    private final ProductoService productoService;


    public ProductoResource(ProductoService productoService) {this.productoService = productoService;
    }


    @Override
    @GetMapping("/productos")
    public ResponseEntity<Map<String, Object>> getAllProductos() {
        log.info("GET /api/v1/producto/productos");
        Map<String, Object> response = new HashMap<>();
        response.put(ConstantesMensajes.PRODUCTOS, productoService.findAll());
        response.put(ConstantesMensajes.MENSAJE, "Productos obtenidos con exito");
        return ResponseEntity.ok(response);

    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductoById(@PathVariable Long id) {
        log.info(" GET /api/v1/producto/{} ", id);
        Map<String, Object> response = new HashMap<>();
        try {
            response.put(ConstantesMensajes.PRODUCTO, productoService.findById(id));
            response.put(ConstantesMensajes.MENSAJE, "Producto obtenido con éxito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @Override
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> createProducto(
            @Parameter(description = "Producto DTO", required = true)
            @RequestBody ProductoDTO productoDTO) {
        log.info(" POST /api/v1/producto");
        Map<String, Object> response = new HashMap<>();
        try{
            response.put(ConstantesMensajes.PRODUCTO, productoService.createProducto(productoDTO));
            response.put(ConstantesMensajes.MENSAJE, "Producto creado con éxito");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NegocioException e) {
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }


    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProducto(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        log.info("PUT /api/v1/producto/{}  ", id);
        Map<String, Object> response = new HashMap<>();
        try{
            response.put(ConstantesMensajes.PRODUCTO, productoService.editProducto(id, productoDTO));
            response.put(ConstantesMensajes.MENSAJE, "Producto actualizado con éxito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProducto(@PathVariable Long id) {
        log.info(" DELETE /api/v1/producto/ {} ", id);
        Map<String, Object> response = new HashMap<>();
        try{
            response.put(ConstantesMensajes.PRODUCTO, productoService.deleteProductoLogico(id));
            response.put(ConstantesMensajes.MENSAJE, "Producto eliminado con éxito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }

    @Override
    @PatchMapping(value= "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadFotoProducto (@PathVariable Long id, @RequestParam("file") final MultipartFile file) {
        log.info("PATCH /api/v1/producto/{}/imagen", id);
        Map<String, Object> response = new HashMap<>();
        try{
            response.put(ConstantesMensajes.PRODUCTO,productoService.subirImagenProducto(id,file));
            response.put(ConstantesMensajes.MENSAJE, "Imagen actualizada con exito:");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (NegocioException e){
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Override
    @GetMapping("/nombre")
    public ResponseEntity<Map<String, Object>> buscarProductoPorNombre(@RequestParam String nombre) {
        log.info("GET /api/v1/producto/nombre");
        Map<String, Object> response = new HashMap<>();
        try{
            response.put(ConstantesMensajes.PRODUCTO, productoService.findByNombre(nombre));
            response.put(ConstantesMensajes.MENSAJE, "Productos obtenidos con éxito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Override
    @GetMapping("/descripcion")
    public ResponseEntity<Map<String, Object>> buscarProductoPorDescripcion(@RequestParam String description) {
        log.info("GET /api/v1/producto/descripcion");
        Map<String, Object> response = new HashMap<>();
        try{
            response.put(ConstantesMensajes.PRODUCTOS, productoService.findByDescripcion(description));
            response.put(ConstantesMensajes.MENSAJE, "Productos obtenidos con éxito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/codigo")
    public ResponseEntity<Map<String, Object>> buscarProductoPorCodigo(@RequestParam String codigo) {
        log.info("GET /api/v1/producto/codigo");
        Map<String, Object> response = new HashMap<>();
        try{
            response.put(ConstantesMensajes.PRODUCTO, productoService.findByCodigo(codigo));
            response.put(ConstantesMensajes.MENSAJE, "Producto obtenidos con éxito");
            return ResponseEntity.ok(response);
        }catch (NegocioException e){
            response.put(ConstantesMensajes.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }



}
