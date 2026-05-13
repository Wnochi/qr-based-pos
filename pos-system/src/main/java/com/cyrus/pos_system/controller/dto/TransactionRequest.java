package com.cyrus.pos_system.controller.dto;

import java.util.List;
import java.math.BigDecimal;

public class TransactionRequest {
    private List<TransactionItemRequest> items;
    private String paymentMethod;
    private BigDecimal paidAmount;

    public TransactionRequest() {
    }

    public List<TransactionItemRequest> getItems() {
        return items;
    }

    public void setItems(List<TransactionItemRequest> items) {
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
}
