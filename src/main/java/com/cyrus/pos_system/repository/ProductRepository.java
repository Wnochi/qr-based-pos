package com.cyrus.pos_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyrus.pos_system.model.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
}
