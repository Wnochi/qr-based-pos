package com.cyrus.pos_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cyrus.pos_system.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
