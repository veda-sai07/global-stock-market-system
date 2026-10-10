package com.example.stock_market.controller;

import com.example.stock_market.model.Stock;
import com.example.stock_market.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /** GET /api/stocks  or  GET /api/stocks?country=India */
    @GetMapping
    public List<Stock> list(@RequestParam(required = false) String country) {
        return stockService.getAll(country);
    }

    @GetMapping("/{symbol}")
    public Stock get(@PathVariable String symbol) {
        return stockService.getBySymbol(symbol);
    }
}
