package com.cedia.smartinventory.controller;

import com.cedia.smartinventory.dto.CategoryRequestDTO;
import com.cedia.smartinventory.dto.CategoryResponseDTO;
import com.cedia.smartinventory.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Gestión de categorías de productos")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Listar categorías")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> listarCategorias() {
        return ResponseEntity.ok(categoryService.listarCategorias());
    }

    @Operation(summary = "Obtener categoría por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> obtenerCategoria(
            @Parameter(description = "ID de la categoría") @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.obtenerCategoria(id));
    }

    @Operation(summary = "Crear categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoría creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> crearCategoria(
            @Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.status(201).body(categoryService.crearCategoria(request));
    }

    @Operation(summary = "Actualizar categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> actualizarCategoria(
            @Parameter(description = "ID de la categoría") @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.ok(categoryService.actualizarCategoria(id, request));
    }

    @Operation(summary = "Eliminar categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(
            @Parameter(description = "ID de la categoría") @PathVariable Long id) {
        categoryService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
