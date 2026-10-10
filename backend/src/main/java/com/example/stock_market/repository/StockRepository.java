package com.example.stock_market.repository;

import com.example.stock_market.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findBySymbolIgnoreCase(String symbol);

    List<Stock> findByCountryIgnoreCase(String country);
}
