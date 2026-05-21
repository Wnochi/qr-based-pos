package com.cyrus.pos_system.controller.dto;

import java.math.BigDecimal;

public class ProductResponse {
    private String id;
    private String name;
    private BigDecimal price;
    private int stock;
    private String photoBase64;
    private String qrCodeBase64;

    public ProductResponse() {
    }

    public ProductResponse(String id, String name, BigDecimal price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public ProductResponse(String id, String name, BigDecimal price, int stock, String photoBase64, String qrCodeBase64) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.photoBase64 = photoBase64;
        this.qrCodeBase64 = qrCodeBase64;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }
}
