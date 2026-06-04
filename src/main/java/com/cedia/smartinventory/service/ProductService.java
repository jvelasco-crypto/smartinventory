package com.cedia.smartinventory.service;

import com.cedia.smartinventory.dto.ProductRequestDTO;
import com.cedia.smartinventory.dto.ProductResponseDTO;
import com.cedia.smartinventory.exception.ResourceNotFoundException;
import com.cedia.smartinventory.model.Product;
import com.cedia.smartinventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDTO crearProducto(ProductRequestDTO request) {
        log.info("Creando producto: nombre='{}', precio={}", request.getName(), request.getPrice());
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .active(true)
                .build();
        Product saved = productRepository.save(product);
        log.info("Producto creado exitosamente con id={}", saved.getId());
        return toResponseDTO(saved);
    }

    public Page<ProductResponseDTO> listarProductos(Pageable pageable) {
        log.debug("Listando productos — página={}, tamaño={}", pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findAll(pageable).map(this::toResponseDTO);
    }

    public ProductResponseDTO obtenerProducto(Long id) {
        log.debug("Buscando producto con id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Producto no encontrado con id={}", id);
                    return new ResourceNotFoundException("Producto no encontrado con id: " + id);
                });
        return toResponseDTO(product);
    }

    public ProductResponseDTO actualizarProducto(Long id, ProductRequestDTO request) {
        log.info("Actualizando producto id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de actualizar producto inexistente id={}", id);
                    return new ResourceNotFoundException("Producto no encontrado con id: " + id);
                });
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        log.info("Producto id={} actualizado correctamente", id);
        return toResponseDTO(productRepository.save(product));
    }

    public void eliminarProducto(Long id) {
        log.info("Eliminando producto id={}", id);
        if (!productRepository.existsById(id)) {
            log.warn("Intento de eliminar producto inexistente id={}", id);
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Producto id={} eliminado correctamente", id);
    }

    private ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .active(product.getActive())
                .build();
    }
}
