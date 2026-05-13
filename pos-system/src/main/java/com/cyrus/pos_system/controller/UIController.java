package com.cyrus.pos_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/pos")
    public String pos() {
        return "redirect:/frontend/index.html";
    }

    @GetMapping("/pos/cashier")
    public String cashier() {
        return "redirect:/frontend/cashier.html";
    }

    @GetMapping("/pos/admin")
    public String admin() {
        return "redirect:/frontend/admin.html";
    }
}
