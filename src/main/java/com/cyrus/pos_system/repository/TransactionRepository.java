package com.cyrus.pos_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.cyrus.pos_system.model.Transaction;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    
    @Query("SELECT DISTINCT t FROM Transaction t LEFT JOIN FETCH t.items ORDER BY t.createdAt DESC")
    List<Transaction> findAllWithItems();
}