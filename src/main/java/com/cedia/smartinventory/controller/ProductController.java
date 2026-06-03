package com.cedia.smartinventory.controller;

import com.cedia.smartinventory.dto.ProductRequestDTO;
import com.cedia.smartinventory.dto.ProductResponseDTO;
import com.cedia.smartinventory.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> crearProducto(
            @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.status(201).body(productService.crearProducto(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> listarProductos() {
        return ResponseEntity.ok(productService.listarProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> obtenerProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productService.obtenerProducto(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.ok(productService.actualizarProducto(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
