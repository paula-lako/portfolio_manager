package com.neueda.portfoliomanager.service;

import com.neueda.portfoliomanager.entity.*;
import com.neueda.portfoliomanager.repository.PortfolioRepository;
import com.neueda.portfoliomanager.repository.StockRepository;
import com.neueda.portfoliomanager.repository.TransactionRepository;
import com.neueda.portfoliomanager.repository.UserStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final StockRepository stockRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserStockRepository userStockRepository;

    public Transaction addTransaction(Long portfolioId, Long stockId, TransactionType type, double amount, double unitPrice) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        // znajdź UserStock w tym portfelu lub stwórz nowy
        UserStock userStock = userStockRepository.findByPortfolioAndStock(portfolio, stock)
                .orElseGet(() -> {
                    UserStock us = new UserStock();
                    us.setPortfolio(portfolio);
                    us.setStock(stock);
                    us.setQuantity(0.0);
                    us.setActualPrice(0.0);
                    return us;
                });

        // aktualizacja liczby akcji
        if (type == TransactionType.BUY) {
            userStock.setQuantity(userStock.getQuantity() + amount);
        } else if (type == TransactionType.SELL) {
            if (userStock.getQuantity() < amount) {
                throw new RuntimeException("Not enough stocks to sell");
            }
            userStock.setQuantity(userStock.getQuantity() - amount);
        }

        // aktualna wartość w tym papierze
        userStock.setActualPrice(userStock.getQuantity() * stock.getCurrentValue());
        userStockRepository.save(userStock);

        // utworzenie transakcji (już tylko z Stock)
        Transaction transaction = new Transaction();
        transaction.setPortfolio(portfolio);
        transaction.setStock(stock);   // ✅ zamiast userStock
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setUnitPrice(unitPrice);
        transaction.setTotalPrice(amount * unitPrice);
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByPortfolio(Long portfolioId) {
        return transactionRepository.findByPortfolioId(portfolioId);
    }
}
