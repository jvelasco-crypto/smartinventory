package com.cedia.smartinventory.controller;

import com.cedia.smartinventory.config.SecurityConfig;
import com.cedia.smartinventory.dto.ProductRequestDTO;
import com.cedia.smartinventory.dto.ProductResponseDTO;
import com.cedia.smartinventory.exception.GlobalExceptionHandler;
import com.cedia.smartinventory.exception.ResourceNotFoundException;
import com.cedia.smartinventory.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    // TEST 2 — Listar productos sin autenticación → 200
    @Test
    void listarProductos_sinAuth_debeRetornar200() throws Exception {
        when(productService.listarProductos(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray());
    }

    // TEST 3 — Crear producto con datos válidos → 201
    @Test
    void crearProducto_datosValidos_debeRetornar201() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Laptop Dell");
        request.setPrice(899.99);
        request.setStock(10);

        ProductResponseDTO response = ProductResponseDTO.builder()
            .id(1L).name("Laptop Dell").price(899.99).stock(10).active(true).build();

        when(productService.crearProducto(any())).thenReturn(response);

        mockMvc.perform(post("/api/products")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Laptop Dell"))
            .andExpect(jsonPath("$.active").value(true));
    }

    // TEST 4 — Crear producto sin nombre → 400
    @Test
    void crearProducto_sinNombre_debeRetornar400() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setPrice(100.0);
        request.setStock(5);

        mockMvc.perform(post("/api/products")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields.name").exists());
    }

    // TEST 5 — Crear producto con precio negativo → 400
    @Test
    void crearProducto_precioNegativo_debeRetornar400() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Producto");
        request.setPrice(-50.0);
        request.setStock(5);

        mockMvc.perform(post("/api/products")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields.price").exists());
    }

    // TEST 6 — Crear producto sin autenticación → 401
    @Test
    void crearProducto_sinAutenticacion_debeRetornar401() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Producto");
        request.setPrice(100.0);
        request.setStock(5);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }

    // TEST 7 — Obtener producto por ID existente → 200
    @Test
    void obtenerProducto_idExistente_debeRetornar200() throws Exception {
        ProductResponseDTO response = ProductResponseDTO.builder()
            .id(1L).name("Laptop Dell").price(899.99).stock(10).active(true).build();

        when(productService.obtenerProducto(1L)).thenReturn(response);

        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Laptop Dell"));
    }

    // TEST 8 — Obtener producto por ID inexistente → 404
    @Test
    void obtenerProducto_idInexistente_debeRetornar404() throws Exception {
        when(productService.obtenerProducto(999L))
            .thenThrow(new ResourceNotFoundException("Producto no encontrado con id: 999"));

        mockMvc.perform(get("/api/products/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Producto no encontrado con id: 999"));
    }

    // TEST 9 — Eliminar sin autenticación → 401
    @Test
    void eliminarProducto_sinAutenticacion_debeRetornar401() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
            .andExpect(status().isUnauthorized());
    }

    // RETO — stock negativo → 400
    @Test
    void crearProducto_stockNegativo_debeRetornar400() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Producto");
        request.setPrice(100.0);
        request.setStock(-5);

        mockMvc.perform(post("/api/products")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields.stock").exists());
    }
}
