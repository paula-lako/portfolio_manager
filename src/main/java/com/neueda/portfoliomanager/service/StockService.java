package com.neueda.portfoliomanager.service;

import com.neueda.portfoliomanager.entity.Stock;
import com.neueda.portfoliomanager.entity.StockHistory;
import com.neueda.portfoliomanager.repository.StockHistoryRepository;
import com.neueda.portfoliomanager.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;

    public Stock getStockById(Long stockId) {
        return stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Double getCurrentValue(Long stockId) {
        return stockHistoryRepository.findTopByStockIdOrderByDateDesc(stockId)
                .map(StockHistory::getCloseValue)
                .orElseThrow(() -> new RuntimeException("No historical data found"));
    }
}