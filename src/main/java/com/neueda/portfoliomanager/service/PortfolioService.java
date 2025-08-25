package com.neueda.portfoliomanager.service;

import com.neueda.portfoliomanager.entity.Portfolio;
import com.neueda.portfoliomanager.entity.Stock;
import com.neueda.portfoliomanager.entity.StockHistory;
import com.neueda.portfoliomanager.entity.TransactionType;
import com.neueda.portfoliomanager.entity.User;
import com.neueda.portfoliomanager.entity.UserStock;
import com.neueda.portfoliomanager.repository.PortfolioRepository;
import com.neueda.portfoliomanager.repository.TransactionRepository;
import com.neueda.portfoliomanager.repository.UserRepository;
import com.neueda.portfoliomanager.repository.UserStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.neueda.portfoliomanager.entity.Transaction;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;
    private final TransactionRepository transactionRepository;

    

    public Portfolio getPortfolio(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
    }

    public List<Portfolio> getPortfoliosByUser(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }

    public Portfolio createPortfolio(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Portfolio portfolio = new Portfolio();
        portfolio.setUser(user);
        return portfolioRepository.save(portfolio);
    }
    public List<UserStock> getUserStocks(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        return userStockRepository.findByPortfolioId(portfolio.getId());
    }

    public double calculateCurrentValue(Portfolio portfolio) {
        List<UserStock> userStocks = userStockRepository.findByPortfolioId(portfolio.getId());

    return userStocks.stream()
            .mapToDouble(us -> us.getQuantity() * us.getStock().getCurrentValue())
            .sum();
    }

    public double calculateReturnRate(Portfolio portfolio) {
        List<UserStock> userStocks = userStockRepository.findByPortfolioId(portfolio.getId());

        double currentValue = userStocks.stream()
                .mapToDouble(us -> us.getQuantity() * us.getStock().getCurrentValue())
                .sum();
    
        // Invested amount = sum of buys from transactions
        double investedAmount = transactionRepository.findByPortfolioId(portfolio.getId()).stream()
                .filter(t -> t.getTransactionType() == TransactionType.BUY)
                .mapToDouble(Transaction::getTotalPrice)
                .sum();
    
        if (investedAmount == 0) return 0.0;
    
        return ((currentValue - investedAmount) / investedAmount) * 100;
    }


    // historical value - needded for the plot
    public List<Map<String, Object>> calculatePortfolioHistory(Portfolio portfolio) {
    List<UserStock> userStocks = userStockRepository.findByPortfolioId(portfolio.getId());

    // Collect all unique dates from the stocks' historical values
    Set<LocalDate> allDates = userStocks.stream()
            .flatMap(us -> us.getStock().getHistoricalValues().stream())
            .map(StockHistory::getDate) // assuming LocalDate
            .collect(Collectors.toSet());

    // Sort dates ascending        
    List<LocalDate> sortedDates = allDates.stream().sorted().toList();
    List<Map<String, Object>> history = new ArrayList<>();

    // For each date, calculate total portfolio value
    for (LocalDate date : sortedDates) {
        double totalValue = userStocks.stream()
                .mapToDouble(us -> {
                    double priceAtDate = us.getStock().getHistoricalValues().stream()
                            .filter(h -> !h.getDate().isAfter(date))
                            .reduce((first, second) -> second)  // last value before date
                            .map(StockHistory::getCloseValue)         // correct: no argument
                            .orElse(us.getStock().getCurrentValue());
                    return us.getQuantity() * priceAtDate;
                })
                .sum();

        Map<String, Object> entry = new HashMap<>();
        entry.put("date", date);
        entry.put("value", totalValue);
        history.add(entry);
    }

    return history;
}
}


