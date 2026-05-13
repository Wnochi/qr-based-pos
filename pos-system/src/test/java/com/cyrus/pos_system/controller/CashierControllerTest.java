package com.cyrus.pos_system.controller;

import com.cyrus.pos_system.model.Product;
import com.cyrus.pos_system.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CashierController.class)
public class CashierControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductRepository productRepository;

    @Test
    public void scanQr_found() throws Exception {
        Product p = new Product();
        p.setId("abc");
        p.setName("Test Product");
        p.setPrice(BigDecimal.valueOf(9.99));
        p.setStock(5);

        when(productRepository.findById("abc")).thenReturn(Optional.of(p));

        mvc.perform(post("/api/cashier/transaction/scan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("qr", "abc"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("abc"))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(9.99))
                .andExpect(jsonPath("$.stock").value(5));
    }

    @Test
    public void scanQr_missingQr_returnsBadRequest() throws Exception {
        mvc.perform(post("/api/cashier/transaction/scan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest());
    }
}
