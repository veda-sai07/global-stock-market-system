package com.example.stock_market.service;

import com.example.stock_market.dto.TradeDtos.*;
import com.example.stock_market.exception.ApiException;
import com.example.stock_market.model.Holding;
import com.example.stock_market.model.Stock;
import com.example.stock_market.model.Transaction;
import com.example.stock_market.model.User;
import com.example.stock_market.repository.HoldingRepository;
import com.example.stock_market.repository.TransactionRepository;
import com.example.stock_market.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TradeService {

    private final UserRepository userRepository;
    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;
    private final StockService stockService;

    public TradeService(UserRepository userRepository, HoldingRepository holdingRepository,
                        TransactionRepository transactionRepository, StockService stockService) {
        this.userRepository = userRepository;
        this.holdingRepository = holdingRepository;
        this.transactionRepository = transactionRepository;
        this.stockService = stockService;
    }

    @Transactional
    public TransactionView buy(Long userId, TradeRequest request) {
        User user = findUser(userId);
        Stock stock = stockService.getBySymbol(request.symbol());
        BigDecimal cost = stock.getCurrentPrice().multiply(BigDecimal.valueOf(request.quantity()));

        if (user.getBalance().compareTo(cost) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Insufficient balance. Needed " + cost + " but you have " + user.getBalance());
        }
        user.setBalance(user.getBalance().subtract(cost));

        Holding holding = holdingRepository.findByUserIdAndStockId(userId, stock.getId()).orElse(null);
        if (holding == null) {
            holding = new Holding();
            holding.setUser(user);
            holding.setStock(stock);
            holding.setQuantity(request.quantity());
            holding.setAveragePrice(stock.getCurrentPrice());
        } else {
            BigDecimal oldValue = holding.getAveragePrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
            int newQty = holding.getQuantity() + request.quantity();
            holding.setAveragePrice(oldValue.add(cost).divide(BigDecimal.valueOf(newQty), 2, RoundingMode.HALF_UP));
            holding.setQuantity(newQty);
        }
        holdingRepository.save(holding);
        userRepository.save(user);
        return toView(record(user, stock, Transaction.Type.BUY, request.quantity()));
    }

    @Transactional
    public TransactionView sell(Long userId, TradeRequest request) {
        User user = findUser(userId);
        Stock stock = stockService.getBySymbol(request.symbol());
        Holding holding = holdingRepository.findByUserIdAndStockId(userId, stock.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "You do not own " + stock.getSymbol()));

        if (holding.getQuantity() < request.quantity()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "You only own " + holding.getQuantity() + " shares of " + stock.getSymbol());
        }
        BigDecimal proceeds = stock.getCurrentPrice().multiply(BigDecimal.valueOf(request.quantity()));
        user.setBalance(user.getBalance().add(proceeds));

        int remaining = holding.getQuantity() - request.quantity();
        if (remaining == 0) {
            holdingRepository.delete(holding);
        } else {
            holding.setQuantity(remaining);
            holdingRepository.save(holding);
        }
        userRepository.save(user);
        return toView(record(user, stock, Transaction.Type.SELL, request.quantity()));
    }

    @Transactional(readOnly = true)
    public PortfolioView portfolio(Long userId) {
        User user = findUser(userId);
        List<HoldingView> views = holdingRepository.findByUserId(userId).stream().map(h -> {
            BigDecimal qty = BigDecimal.valueOf(h.getQuantity());
            BigDecimal marketValue = h.getStock().getCurrentPrice().multiply(qty);
            BigDecimal profit = marketValue.subtract(h.getAveragePrice().multiply(qty));
            return new HoldingView(h.getStock().getSymbol(), h.getStock().getCompanyName(), h.getQuantity(),
                    h.getAveragePrice(), h.getStock().getCurrentPrice(), marketValue, profit);
        }).toList();

        BigDecimal invested = views.stream().map(HoldingView::marketValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PortfolioView(user.getBalance(), invested, user.getBalance().add(invested), views);
    }

    @Transactional(readOnly = true)
    public List<TransactionView> history(Long userId) {
        findUser(userId);
        return transactionRepository.findByUserIdOrderByTimestampDesc(userId).stream().map(this::toView).toList();
    }

    private Transaction record(User user, Stock stock, Transaction.Type type, int quantity) {
        Transaction t = new Transaction();
        t.setUser(user);
        t.setStock(stock);
        t.setType(type);
        t.setQuantity(quantity);
        t.setPrice(stock.getCurrentPrice());
        return transactionRepository.save(t);
    }

    private TransactionView toView(Transaction t) {
        return new TransactionView(t.getId(), t.getType(), t.getStock().getSymbol(), t.getQuantity(),
                t.getPrice(), t.getPrice().multiply(BigDecimal.valueOf(t.getQuantity())), t.getTimestamp());
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
