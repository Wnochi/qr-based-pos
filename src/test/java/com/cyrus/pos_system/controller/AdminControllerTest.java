package com.cyrus.pos_system.controller;

import com.cyrus.pos_system.model.Transaction;
import com.cyrus.pos_system.model.TransactionItem;
import com.cyrus.pos_system.repository.TransactionRepository;
import com.cyrus.pos_system.repository.ProductRepository;
import com.cyrus.pos_system.service.QRService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private QRService qrService;

    @Test
    public void transactionsEndpointReturnsExpectedFields() throws Exception {
        Transaction tx = new Transaction();
        tx.setId("tx-1");
        tx.setCreatedAt(Instant.parse("2026-05-22T10:00:00Z"));
        tx.setTotal(new BigDecimal("120.50"));

        TransactionItem it = new TransactionItem();
        it.setProductId("p1");
        it.setName("Test Product");
        it.setUnitPrice(new BigDecimal("60.25"));
        it.setQuantity(2);

        tx.addItem(it);

        when(transactionRepository.findAllWithItems()).thenReturn(List.of(tx));

        mockMvc.perform(get("/api/admin/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("tx-1"))
                .andExpect(jsonPath("$[0].createdAt").value("2026-05-22T10:00:00Z"))
                .andExpect(jsonPath("$[0].totalAmount").value(120.50))
                .andExpect(jsonPath("$[0].itemsCount").value(2))
                .andExpect(jsonPath("$[0].items[0].productId").value("p1"))
                .andExpect(jsonPath("$[0].items[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].items[0].unitPrice").value(60.25))
                .andExpect(jsonPath("$[0].items[0].quantity").value(2))
                .andExpect(jsonPath("$[0].items[0].subtotal").value(120.50));
    }
}
