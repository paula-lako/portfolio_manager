package com.neueda.portfoliomanager.controllers;

import com.neueda.portfoliomanager.entity.Stock;
import com.neueda.portfoliomanager.entity.StockTicker;
import com.neueda.portfoliomanager.repository.StockRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockRepository stockRepository;

    public StockController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @GetMapping
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<Stock> getStock(@PathVariable String  ticker)
    {
        try {
            // Konwersja String -> Enum
            StockTicker stockTicker = StockTicker.valueOf(ticker.toUpperCase());

            return stockRepository.findByTicker(stockTicker)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());

        } catch (IllegalArgumentException e) {
            // gdy użytkownik poda ticker spoza enuma
            return ResponseEntity.badRequest().build();
        }
    }
}