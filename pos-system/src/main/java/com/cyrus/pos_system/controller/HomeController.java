package com.cyrus.pos_system.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "message", "Welcome to pos-system",
                "endpoints", Map.of(
                        "adminInventory", "/api/admin/inventory",
                        "addProduct", "/api/admin/products (POST)",
                        "generateQr", "/api/admin/products/{id}/qr (POST)",
                        "scanQr", "/api/cashier/transaction/scan (POST)"
                )
        );
    }
}
