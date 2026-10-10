package com.example.stock_market.dto;

import com.example.stock_market.model.Transaction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TradeDtos {

    public record TradeRequest(
            @NotBlank String symbol,
            @Min(value = 1, message = "quantity must be at least 1") int quantity) {
    }

    public record HoldingView(String symbol, String companyName, int quantity,
                              BigDecimal averagePrice, BigDecimal currentPrice,
                              BigDecimal marketValue, BigDecimal profitLoss) {
    }

    public record PortfolioView(BigDecimal cashBalance, BigDecimal investedValue,
                                BigDecimal totalValue, List<HoldingView> holdings) {
    }

    public record TransactionView(Long id, Transaction.Type type, String symbol, int quantity,
                                  BigDecimal price, BigDecimal total, LocalDateTime timestamp) {
    }
}
