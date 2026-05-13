package com.cyrus.pos_system.controller;

import com.cyrus.pos_system.model.Product;
import com.cyrus.pos_system.repository.ProductRepository;
import com.cyrus.pos_system.service.QRService;
import com.google.zxing.WriterException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final ProductRepository productRepository;
    private final QRService qrService;

    public AdminController(ProductRepository productRepository, QRService qrService) {
        this.productRepository = productRepository;
        this.qrService = qrService;
    }

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product input) {
        if (input.getPrice() == null) input.setPrice(BigDecimal.ZERO);
        if (input.getId() == null || input.getId().isBlank()) {
            input.setId(UUID.randomUUID().toString());
        }
        Product saved = productRepository.save(input);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/inventory")
    public List<Product> inventory() {
        return productRepository.findAll();
    }

    @PostMapping("/products/{id}/qr")
    public ResponseEntity<?> generateQr(@PathVariable String id) throws WriterException, IOException {
        Optional<Product> p = productRepository.findById(id);
        if (p.isEmpty()) return ResponseEntity.notFound().build();
        String path = qrService.generateQRCodeForId(id);
        Product prod = p.get();
        prod.setQrCodePath(path);
        productRepository.save(prod);
        return ResponseEntity.ok(path);
    }
}
