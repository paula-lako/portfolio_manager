package com.neueda.portfoliomanager.controllers;

import com.neueda.portfoliomanager.entity.Portfolio;
import com.neueda.portfoliomanager.entity.UserStock;
import com.neueda.portfoliomanager.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/portfolios")
//@RequiredArgsConstructor
//public class PortfolioController {
//
//    private final PortfolioService portfolioService;
//
//    // Pobranie wszystkich portfeli użytkownika
//    @GetMapping("/users/{userId}")
//    public ResponseEntity<List<Portfolio>> getUserPortfolios(@PathVariable Long userId) {
//        return ResponseEntity.ok(portfolioService.getPortfoliosByUser(userId));
//    }
//
//    // Pobranie jednego portfela
//    @GetMapping("/{portfolioId}")
//    public ResponseEntity<Portfolio> getPortfolio(@PathVariable Long portfolioId) {
//        return ResponseEntity.ok(portfolioService.getPortfolio(portfolioId));
//    }
//    @GetMapping("/{portfolioId}/stocks")
//    public List<UserStock> getUserStocks(@PathVariable Long portfolioId) {
//        return portfolioService.getUserStocks(portfolioId);
//    }
//
//    // Utworzenie nowego portfela dla użytkownika
//    @PostMapping("/users/{userId}")
//    public ResponseEntity<Portfolio> createPortfolio(@PathVariable Long userId) {
//        Portfolio portfolio = portfolioService.createPortfolio(userId);
//        return new ResponseEntity<>(portfolio, HttpStatus.CREATED);
//    }

    // @GetMapping("/{id}/value")
    // public double getPortfolioValue(@PathVariable Long id) {
    //     Portfolio portfolio = portfolioService.getPortfolio(id);
    //     return portfolioService.calculateCurrentValue(portfolio);
    // }

    // @GetMapping("/{id}/history")
    // public List<PortfolioValueDTO> getPortfolioHistory(@PathVariable Long id) {
    //     return portfolioService.calculateHistoricalValues(id);
    // }


//}