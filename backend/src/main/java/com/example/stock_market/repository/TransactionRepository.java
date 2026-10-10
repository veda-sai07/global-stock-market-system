package com.example.stock_market.repository;

import com.example.stock_market.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByTimestampDesc(Long userId);
}
