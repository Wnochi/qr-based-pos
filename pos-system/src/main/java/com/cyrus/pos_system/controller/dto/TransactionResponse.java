package com.cyrus.pos_system.controller.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class TransactionResponse {
    private String id;
    private Instant createdAt;
    private BigDecimal total;
    private List<TransactionItemResponse> items;
    private String paymentMethod;
    private BigDecimal paidAmount;
    private String receipt;

    public TransactionResponse() {
    }

    public TransactionResponse(String id, Instant createdAt, BigDecimal total, List<TransactionItemResponse> items) {
        this.id = id;
        this.createdAt = createdAt;
        this.total = total;
        this.items = items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<TransactionItemResponse> getItems() {
        return items;
    }

    public void setItems(List<TransactionItemResponse> items) {
        this.items = items;
    }
}
