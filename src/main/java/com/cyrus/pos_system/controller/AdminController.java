package com.cyrus.pos_system.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cyrus.pos_system.model.Product;
import com.cyrus.pos_system.model.Transaction;
import com.cyrus.pos_system.repository.ProductRepository;
import com.cyrus.pos_system.repository.TransactionRepository;
import com.cyrus.pos_system.service.QRService;
import com.google.zxing.WriterException;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    private final ProductRepository productRepository;
    private final QRService qrService;
    private final TransactionRepository transactionRepository;

    public AdminController(ProductRepository productRepository, QRService qrService, TransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.qrService = qrService;
        this.transactionRepository = transactionRepository;
    }

    public record MonthlyReportResponse(LocalDate date, String transactionNo, String productName, int quantity, BigDecimal amount) {}
    public record YearlyReportResponse(String month, BigDecimal revenue, long itemsSold) {}

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "threshold", required = false) Integer threshold,
            @RequestParam(value = "qrCodeBase64", required = false) String qrCodeBase64,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        
        Product input;
        boolean isEdit = (id != null && !id.isBlank());
        
        if (isEdit) {
            input = productRepository.findById(id).orElse(new Product());
        } else {
            input = new Product();
            input.setId(UUID.randomUUID().toString());
        }
        
        input.setName(name);
        input.setPrice(price != null ? price : BigDecimal.ZERO);
        input.setStock(stock);
        input.setThreshold(threshold != null ? threshold : 5);
        if (qrCodeBase64 != null) input.setQrCodeBase64(qrCodeBase64);
        
        // --- 1. HANDLE QR GENERATION LOGIC ---
        if (!isEdit || input.getQrCodeBase64() == null || input.getQrCodeBase64().isBlank()) {
            try {
                String base64 = qrService.generateQRCodeBase64ForId(input.getId());
                input.setQrCodeBase64(base64);
            } catch (Exception e) {
                System.err.println("ZXing generation failure: " + e.getMessage());
            }
        }
        
        // --- 2. THE STANDARD APPROACH: BINARY IMAGE STORAGE SYSTEM ---
        if (image != null && !image.isEmpty()) {
            try {
                byte[] bytes = image.getBytes();
                String base64 = Base64.getEncoder().encodeToString(bytes);
                input.setPhotoBase64(base64);
            } catch (IOException e) {
                System.err.println("Multipart system conversion to Base64 failure: " + e.getMessage());
            }
        }
        
        Product saved = productRepository.save(input);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/inventory")
    public List<Product> inventory() {
        return productRepository.findAll();
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Map<String, Object>>> transactions() {
        List<Transaction> allTransactions = transactionRepository.findAllWithItems();
        List<Map<String, Object>> out = new ArrayList<>();

        for (Transaction tx : allTransactions) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", tx.getId());
            m.put("createdAt", tx.getCreatedAt() != null ? tx.getCreatedAt().toString() : null);
            m.put("totalAmount", tx.getTotal() != null ? tx.getTotal() : BigDecimal.ZERO);

            // itemsCount: sum of quantities across items (matches frontend expectation)
            int itemsCount = 0;
            if (tx.getItems() != null) {
                for (var it : tx.getItems()) {
                    try {
                        itemsCount += it.getQuantity();
                    } catch (Exception ex) { /* ignore malformed item */ }
                }
            }
            m.put("itemsCount", itemsCount);

            // include item details and other useful fields for the frontend
            m.put("items", tx.getItems());
            m.put("paymentMethod", tx.getPaymentMethod());
            m.put("paidAmount", tx.getPaidAmount());

            out.add(m);
        }

        return ResponseEntity.ok(out);
    }

    @PostMapping("/products/{id}/qr")
    public ResponseEntity<?> generateQr(@PathVariable String id) throws WriterException, IOException {
        Optional<Product> p = productRepository.findById(id);
        if (p.isEmpty()) return ResponseEntity.notFound().build();
        String base64 = qrService.generateQRCodeBase64ForId(id);
        Product prod = p.get();
        prod.setQrCodeBase64(base64);
        productRepository.save(prod);
        return ResponseEntity.ok(base64);
    }
    
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        return productRepository.findById(id).map(product -> {
            productRepository.delete(product);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- 📊 SALES REPORTING ENDPOINTS ---

    @GetMapping("/reports/monthly")
    public ResponseEntity<List<MonthlyReportResponse>> getMonthlyReport(
            @RequestParam int year, 
            @RequestParam int month) {
        
        List<Transaction> allTransactions = transactionRepository.findAllWithItems();
        List<MonthlyReportResponse> detailedLogs = new ArrayList<>();

        for (Transaction tx : allTransactions) {
            if (tx.getCreatedAt() == null) continue;
            
            LocalDate txDate = tx.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
            
            if (txDate.getYear() == year && txDate.getMonthValue() == month) {
                tx.getItems().forEach(item -> {
                    BigDecimal itemTotalPrice = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    detailedLogs.add(new MonthlyReportResponse(
                        txDate,
                        tx.getId(),
                        item.getName(),
                        item.getQuantity(),
                        itemTotalPrice
                    ));
                });
            }
        }
        return ResponseEntity.ok(detailedLogs);
    }

    @GetMapping("/reports/yearly")
    public ResponseEntity<List<YearlyReportResponse>> getYearlyReport(@RequestParam int year) {
        List<Transaction> allTransactions = transactionRepository.findAllWithItems();
        
        String[] shortMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        Map<Integer, BigDecimal> monthlyRevenueMap = new HashMap<>();
        Map<Integer, Long> monthlyItemsSoldMap = new HashMap<>();
        
        for (int i = 1; i <= 12; i++) {
            monthlyRevenueMap.put(i, BigDecimal.ZERO);
            monthlyItemsSoldMap.put(i, 0L);
        }

        for (Transaction tx : allTransactions) {
            if (tx.getCreatedAt() == null) continue;
            LocalDate txDate = tx.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
            
            if (txDate.getYear() == year) {
                int mNum = txDate.getMonthValue();
                
                BigDecimal grossTotal = tx.getTotal() != null ? tx.getTotal() : BigDecimal.ZERO;
                monthlyRevenueMap.put(mNum, monthlyRevenueMap.get(mNum).add(grossTotal));
                
                long itemsCountInTx = tx.getItems().stream().mapToLong(item -> item.getQuantity()).sum();
                monthlyItemsSoldMap.put(mNum, monthlyItemsSoldMap.get(mNum) + itemsCountInTx);
            }
        }

        List<YearlyReportResponse> cleanTimeline = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            cleanTimeline.add(new YearlyReportResponse(
                shortMonths[i - 1],
                monthlyRevenueMap.get(i),
                monthlyItemsSoldMap.get(i)
            ));
        }

        return ResponseEntity.ok(cleanTimeline);
    }
}