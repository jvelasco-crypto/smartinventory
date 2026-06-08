package com.cedia.smartinventory.controller;

import com.cedia.smartinventory.dto.ProductResponseDTO;
import com.cedia.smartinventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Reportes de inventario")
public class ReportController {

    private final ProductService productService;

    @Operation(summary = "Productos con stock bajo",
               description = "Retorna productos cuyo stock es menor o igual al umbral indicado")
    @ApiResponse(responseCode = "200", description = "Reporte generado correctamente")
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDTO>> lowStock(
            @Parameter(description = "Umbral de stock (por defecto 10)")
            @RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(productService.listarLowStock(threshold));
    }
}
