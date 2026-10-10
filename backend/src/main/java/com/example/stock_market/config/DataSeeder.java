package com.example.stock_market.config;

import com.example.stock_market.model.Stock;
import com.example.stock_market.repository.StockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/** Fills the stocks table with sample global stocks the first time the app starts. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final StockRepository stockRepository;

    public DataSeeder(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public void run(String... args) {
        if (stockRepository.count() > 0) {
            return;
        }
        stockRepository.saveAll(List.of(
                new Stock("AAPL", "Apple Inc.", "NASDAQ", "USA", "225.40"),
                new Stock("MSFT", "Microsoft Corporation", "NASDAQ", "USA", "415.10"),
                new Stock("GOOGL", "Alphabet Inc.", "NASDAQ", "USA", "172.65"),
                new Stock("TSLA", "Tesla, Inc.", "NASDAQ", "USA", "248.30"),
                new Stock("RELIANCE", "Reliance Industries", "NSE", "India", "2950.75"),
                new Stock("TCS", "Tata Consultancy Services", "NSE", "India", "4120.50"),
                new Stock("INFY", "Infosys Limited", "NSE", "India", "1865.20"),
                new Stock("7203", "Toyota Motor Corporation", "TSE", "Japan", "2685.00"),
                new Stock("SAP", "SAP SE", "XETRA", "Germany", "198.45"),
                new Stock("HSBA", "HSBC Holdings plc", "LSE", "UK", "7.12")
        ));
    }
}
