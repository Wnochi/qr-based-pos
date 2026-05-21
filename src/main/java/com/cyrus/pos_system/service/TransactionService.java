package com.cyrus.pos_system.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cyrus.pos_system.controller.dto.TransactionItemRequest;
import com.cyrus.pos_system.model.Product;
import com.cyrus.pos_system.model.Transaction;
import com.cyrus.pos_system.model.TransactionItem;
import com.cyrus.pos_system.repository.ProductRepository;
import com.cyrus.pos_system.repository.TransactionRepository;

@Service
public class TransactionService {
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(ProductRepository productRepository, TransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional 
    public Transaction processTransaction(List<TransactionItemRequest> itemsReq, String paymentMethod, java.math.BigDecimal paidAmount) {
        if (itemsReq == null || itemsReq.isEmpty()) throw new IllegalArgumentException("no items provided");

        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID().toString());
        tx.setCreatedAt(Instant.now());

        BigDecimal total = BigDecimal.ZERO;

        for (TransactionItemRequest ir : itemsReq) {
            String pid = ir.getProductId();
            int qty = ir.getQuantity();
            if (pid == null || pid.isBlank() || qty <= 0) throw new IllegalArgumentException("invalid item");

            Product prod = productRepository.findById(pid)
                    .orElseThrow(() -> new IllegalArgumentException("product not found: " + pid));

            if (prod.getStock() < qty) throw new IllegalArgumentException("insufficient stock for: " + pid);

            prod.setStock(prod.getStock() - qty);
            productRepository.save(prod);

            TransactionItem ti = new TransactionItem();
            ti.setProductId(prod.getId());
            ti.setName(prod.getName());
            ti.setUnitPrice(prod.getPrice());
            ti.setQuantity(qty);
            
            ti.setTransaction(tx); 
            tx.addItem(ti);

            BigDecimal subtotal = prod.getPrice().multiply(BigDecimal.valueOf(qty));
            total = total.add(subtotal);
        }

        tx.setTotal(total);
        tx.setPaymentMethod(paymentMethod);
        tx.setPaidAmount(paidAmount);

        if (paidAmount == null || paidAmount.compareTo(total) < 0) {
            throw new IllegalArgumentException("insufficient payment");
        }

        return transactionRepository.save(tx);
    }

    public String formatReceipt(Transaction tx) {
        StringBuilder sb = new StringBuilder();
        sb.append("*** RECEIPT ***\n");
        sb.append("Transaction: ").append(tx.getId()).append("\n");
        sb.append("Date: ").append(tx.getCreatedAt()).append("\n\n");
        sb.append("Items:\n");
        tx.getItems().forEach(i -> {
            java.math.BigDecimal subtotal = i.getUnitPrice().multiply(java.math.BigDecimal.valueOf(i.getQuantity()));
            sb.append(String.format("%s x%d  @ %s  = %s\n", i.getName(), i.getQuantity(), i.getUnitPrice(), subtotal));
        });
        sb.append("\nTotal: ").append(tx.getTotal()).append("\n");
        sb.append("Paid (").append(tx.getPaymentMethod()).append("): ").append(tx.getPaidAmount()).append("\n");
        sb.append("Change: ").append(tx.getPaidAmount().subtract(tx.getTotal())).append("\n");
        sb.append("*** THANK YOU ***\n");
        return sb.toString();
    }
}