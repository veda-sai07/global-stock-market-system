package com.example.stock_market.controller;

import com.example.stock_market.dto.TradeDtos.*;
import com.example.stock_market.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/buy")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionView buy(@PathVariable Long userId, @Valid @RequestBody TradeRequest request) {
        return tradeService.buy(userId, request);
    }

    @PostMapping("/sell")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionView sell(@PathVariable Long userId, @Valid @RequestBody TradeRequest request) {
        return tradeService.sell(userId, request);
    }

    @GetMapping("/portfolio")
    public PortfolioView portfolio(@PathVariable Long userId) {
        return tradeService.portfolio(userId);
    }

    @GetMapping("/transactions")
    public List<TransactionView> transactions(@PathVariable Long userId) {
        return tradeService.history(userId);
    }
}
