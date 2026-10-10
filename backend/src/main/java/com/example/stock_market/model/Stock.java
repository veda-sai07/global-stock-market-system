package com.example.stock_market.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "stocks")
@Getter
@Setter
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol;

    @Column(nullable = false)
    private String companyName;

    private String exchange;

    private String country;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentPrice;

    public Stock(String symbol, String companyName, String exchange, String country, String price) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.exchange = exchange;
        this.country = country;
        this.currentPrice = new BigDecimal(price);
    }
}
