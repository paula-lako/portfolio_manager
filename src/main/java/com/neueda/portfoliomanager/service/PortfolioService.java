package com.neueda.portfoliomanager.service;

import com.neueda.portfoliomanager.entity.Portfolio;
import com.neueda.portfoliomanager.entity.User;
import com.neueda.portfoliomanager.entity.UserStock;
import com.neueda.portfoliomanager.repository.PortfolioRepository;
import com.neueda.portfoliomanager.repository.UserRepository;
import com.neueda.portfoliomanager.repository.UserStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;

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
}
