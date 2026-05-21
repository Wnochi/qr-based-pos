package com.cyrus.pos_system.controller.dto;

public class TransactionItemRequest {
    private String productId;
    private int quantity;

    public TransactionItemRequest() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
