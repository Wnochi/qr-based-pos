package com.cyrus.pos_system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cyrus.pos_system.controller.dto.ProductResponse;
import com.cyrus.pos_system.controller.dto.ScanRequest;
import com.cyrus.pos_system.controller.dto.TransactionItemResponse;
import com.cyrus.pos_system.controller.dto.TransactionRequest;
import com.cyrus.pos_system.controller.dto.TransactionResponse;
import com.cyrus.pos_system.model.Transaction;
import com.cyrus.pos_system.repository.ProductRepository;
import com.cyrus.pos_system.service.TransactionService;

@RestController
@RequestMapping("/api/cashier")
public class CashierController {
    private final ProductRepository productRepository;
    private final TransactionService transactionService;

    public CashierController(ProductRepository productRepository, TransactionService transactionService) {
        this.productRepository = productRepository;
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction/scan")
    public ResponseEntity<?> scanQr(@RequestBody ScanRequest body) {
        String qr = body.getQr();
        if (qr == null || qr.isBlank()) return ResponseEntity.badRequest().body("qr required");
        return productRepository.findById(qr)
                .map(prod -> ResponseEntity.ok(new ProductResponse(
                        prod.getId(),
                        prod.getName(),
                        prod.getPrice(),
                        prod.getStock()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/transaction/complete")
    public ResponseEntity<?> completeTransaction(@RequestBody TransactionRequest req) {
        try {
            Transaction tx = transactionService.processTransaction(req.getItems(), req.getPaymentMethod(), req.getPaidAmount());
            var items = tx.getItems().stream().map(i -> new TransactionItemResponse(
                    i.getProductId(), i.getName(), i.getUnitPrice(), i.getQuantity(), i.getUnitPrice().multiply(java.math.BigDecimal.valueOf(i.getQuantity()))
            )).toList();
            TransactionResponse resp = new TransactionResponse(tx.getId(), tx.getCreatedAt(), tx.getTotal(), items);
            resp.setPaymentMethod(tx.getPaymentMethod());
            resp.setPaidAmount(tx.getPaidAmount());
            resp.setReceipt(transactionService.formatReceipt(tx));
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", ex.getMessage()));
        }
    }
}
