package com.cedia.smartinventory;

import com.cedia.smartinventory.dto.ProductRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // TEST 1 — Health check end-to-end
    @Test
    void healthCheck_debeRetornarStatusUp() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"));
    }

    // TEST 2 — Crear y recuperar — flujo completo con BD real (H2)
    @Test
    void crearYRecuperarProducto_flujoCompleto() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Laptop Dell Integration");
        request.setPrice(999.99);
        request.setStock(5);

        String responseBody = mockMvc.perform(post("/api/products")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Laptop Dell Integration"))
            .andExpect(jsonPath("$.active").value(true))
            .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(responseBody).get("id").asLong();

        mockMvc.perform(get("/api/products/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Laptop Dell Integration"))
            .andExpect(jsonPath("$.active").value(true));
    }

    // TEST 3 — Validación end-to-end
    @Test
    void crearProducto_sinNombre_debeRetornar400() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setPrice(100.0);
        request.setStock(5);

        mockMvc.perform(post("/api/products")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields.name").exists());
    }

    // TEST 4 — 404 real desde la BD
    @Test
    void obtenerProducto_idInexistente_debeRetornar404() throws Exception {
        mockMvc.perform(get("/api/products/99999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
