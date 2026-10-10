package com.example.stock_market.service;

import com.example.stock_market.exception.ApiException;
import com.example.stock_market.model.Stock;
import com.example.stock_market.repository.StockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> getAll(String country) {
        if (country == null || country.isBlank()) {
            return stockRepository.findAll();
        }
        return stockRepository.findByCountryIgnoreCase(country);
    }

    public Stock getBySymbol(String symbol) {
        return stockRepository.findBySymbolIgnoreCase(symbol)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Stock not found: " + symbol));
    }
}
