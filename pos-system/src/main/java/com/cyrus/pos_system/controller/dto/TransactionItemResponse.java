package com.cyrus.pos_system.controller.dto;

import java.math.BigDecimal;

public class TransactionItemResponse {
    private String productId;
    private String name;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;

    public TransactionItemResponse() {
    }

    public TransactionItemResponse(String productId, String name, BigDecimal unitPrice, int quantity, BigDecimal subtotal) {
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
