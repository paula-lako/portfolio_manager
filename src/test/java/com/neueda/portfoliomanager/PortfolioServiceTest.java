package com.neueda.portfoliomanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.neueda.portfoliomanager.entity.*;
import com.neueda.portfoliomanager.repository.*;
import com.neueda.portfoliomanager.service.PortfolioService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class PortfolioServiceTest {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    void testCalculateCurrentValue() {
        // Arrange
        Portfolio portfolio = new Portfolio();
        portfolioRepository.save(portfolio);

        Stock stock = new Stock();
        stock.setTicker("AAPL");
        stock.setCurrentValue(200.0); // current market price

        Transaction t1 = new Transaction();
        t1.setPortfolio(portfolio);
        t1.setStock(stock);
        t1.setTransactionType(TransactionType.BUY);
        t1.setAmount(5); // bought 5 shares
        t1.setUnitPrice(150.0);

        Transaction t2 = new Transaction();
        t2.setPortfolio(portfolio);
        t2.setStock(stock);
        t2.setTransactionType(TransactionType.SELL);
        t2.setAmount(2); // sold 2 shares
        t2.setUnitPrice(180.0);

        transactionRepository.saveAll(List.of(t1, t2));

        // Act
        double currentValue = portfolioService.calculateCurrentValue(portfolio);

        // Assert
        // Net shares = 5 - 2 = 3
        // Current price = 200
        // Expected value = 3 * 200 = 600
        assertEquals(500.0, currentValue, 0.01);
    }
}
