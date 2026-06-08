package com.cedia.smartinventory.service;

import com.cedia.smartinventory.dto.CategoryRequestDTO;
import com.cedia.smartinventory.dto.CategoryResponseDTO;
import com.cedia.smartinventory.exception.ResourceNotFoundException;
import com.cedia.smartinventory.model.Category;
import com.cedia.smartinventory.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDTO crearCategoria(CategoryRequestDTO request) {
        log.info("Creando categoría: nombre='{}'", request.getName());
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        Category saved = categoryRepository.save(category);
        log.info("Categoría creada con id={}", saved.getId());
        return toResponseDTO(saved);
    }

    public List<CategoryResponseDTO> listarCategorias() {
        log.debug("Listando todas las categorías");
        return categoryRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    public CategoryResponseDTO obtenerCategoria(Long id) {
        log.debug("Buscando categoría con id={}", id);
        return toResponseDTO(findOrThrow(id));
    }

    public CategoryResponseDTO actualizarCategoria(Long id, CategoryRequestDTO request) {
        log.info("Actualizando categoría id={}", id);
        Category category = findOrThrow(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        log.info("Categoría id={} actualizada correctamente", id);
        return toResponseDTO(categoryRepository.save(category));
    }

    public void eliminarCategoria(Long id) {
        log.info("Eliminando categoría id={}", id);
        if (!categoryRepository.existsById(id)) {
            log.warn("Intento de eliminar categoría inexistente id={}", id);
            throw new ResourceNotFoundException("Categoría no encontrada con id: " + id);
        }
        categoryRepository.deleteById(id);
        log.info("Categoría id={} eliminada correctamente", id);
    }

    private Category findOrThrow(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Categoría no encontrada con id={}", id);
            return new ResourceNotFoundException("Categoría no encontrada con id: " + id);
        });
    }

    private CategoryResponseDTO toResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
