package ar.edu.unju.fi.poo.tp8poo.controller;

import ar.edu.unju.fi.poo.tp8poo.dto.ProductoDTO;
import ar.edu.unju.fi.poo.tp8poo.service.ProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Slf4j
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {this.productoService = productoService;}

    @GetMapping("")
    public ResponseEntity<List<ProductoDTO>> getAllProductos() {
        List<ProductoDTO> productos= productoService.findAll();
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.ok(productos);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id) {
        ProductoDTO productoDTO = productoService.findById(id);
        if (productoDTO == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(productoDTO);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ProductoDTO> createProducto(@RequestBody ProductoDTO productoDTO) {
        ProductoDTO createdProducto = productoService.createProducto(productoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProducto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductoDTO> updateProducto(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        ProductoDTO updatedProducto = productoService.editProducto(id, productoDTO);
        if (updatedProducto == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(updatedProducto);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ProductoDTO> deleteProducto(@PathVariable Long id) {
        ProductoDTO producto = productoService.deleteProductoLogico(id);
        if (producto==null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(producto);
        }
    }



}
