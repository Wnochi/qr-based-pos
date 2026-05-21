package com.cyrus.pos_system.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cyrus.pos_system.controller.dto.TransactionItemRequest;
import com.cyrus.pos_system.model.Product;
import com.cyrus.pos_system.model.Transaction;
import com.cyrus.pos_system.repository.ProductRepository;
import com.cyrus.pos_system.service.QRService;
import com.cyrus.pos_system.service.TransactionService;

@RestController
@RequestMapping("/scan")
public class QRScanController {

    private final QRService qrService;
    private final TransactionService transactionService;
    private final ProductRepository productRepository;

    public QRScanController(QRService qrService, TransactionService transactionService, ProductRepository productRepository) {
        this.qrService = qrService;
        this.transactionService = transactionService;
        this.productRepository = productRepository;
    }

    // Simple scan endpoint: upload a PNG/JPG containing a QR and get the decoded string
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> scanUpload(@RequestParam("file") MultipartFile file) {
        try {
            String decoded = qrService.decode(file);
            return ResponseEntity.ok(decoded);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to decode QR: " + e.getMessage());
        }
    }

    @PostMapping(value = "/checkout", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> scanAndCheckout(@RequestParam("file") MultipartFile file) {
        try {
            String decoded = qrService.decode(file);
            if (decoded == null || decoded.isBlank()) {
                return ResponseEntity.badRequest().body("No QR content");
            }

            Optional<Product> p = productRepository.findById(decoded);
            if (p.isEmpty()) return ResponseEntity.badRequest().body("Product not found: " + decoded);
            Product prod = p.get();
            if (prod.getPrice() == null) return ResponseEntity.badRequest().body("Product price unknown");

            TransactionItemRequest item = new TransactionItemRequest();
            item.setProductId(prod.getId());
            item.setQuantity(1);

            Transaction tx = transactionService.processTransaction(List.of(item), "CASH", prod.getPrice());
            String receipt = transactionService.formatReceipt(tx);
            return ResponseEntity.ok(receipt);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error decoding or processing: " + e.getMessage());
        }
    }
}
