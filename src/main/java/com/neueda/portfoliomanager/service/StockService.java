package com.neueda.portfoliomanager.service;

import com.neueda.portfoliomanager.entity.Stock;
import com.neueda.portfoliomanager.exceptions.InvalidStockException;
import com.neueda.portfoliomanager.exceptions.StockNotFoundException;
import com.neueda.portfoliomanager.entity.StockHistory;
import com.neueda.portfoliomanager.repository.StockHistoryRepository;
import com.neueda.portfoliomanager.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    public Stock getStockById(Long stockId) {
        return stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Stock getStockById(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Stock with id " + id + " not found"));
    }

    public Stock getStockByTicker(String ticker) {
        return stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new StockNotFoundException("Stock with ticker " + ticker + " not found"));
    public Double getCurrentValue(Long stockId) {
        return stockHistoryRepository.findTopByStockIdOrderByDateDesc(stockId)
                .map(StockHistory::getCloseValue)
                .orElseThrow(() -> new RuntimeException("No historical data found"));
    }

    public Stock createStock(Stock stock) {
        if (stock.getTicker() == null || stock.getTicker().isBlank()) {
            throw new InvalidStockException("Stock ticker cannot be empty");
        }
        if (stock.getName() == null || stock.getName().isBlank()) {
            throw new InvalidStockException("Stock name cannot be empty");
        }
        if (stock.getStockType() == null) {
            throw new InvalidStockException("Stock type cannot be empty");
        }
        if (stock.getCurrentValue() == null) {
            throw new InvalidStockException("Stock current value cannot be empty");
        }

        if (stockRepository.existsByTicker(stock.getTicker())) {
            throw new InvalidStockException(
                    "Stock with ticker '" + stock.getTicker() + "' already exists"
            );
        }

        return stockRepository.save(stock);
    }

    public void deleteStock(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new StockNotFoundException("Stock with id " + id + " not found");
        }
        stockRepository.deleteById(id);
    }

    // Update specific fields (for /PATCH method)
    public void updateModifiedFields(Stock existingStock, Stock newStock) {
        if (newStock.getTicker() != null && !newStock.getTicker().isBlank()) {
            existingStock.setTicker(newStock.getTicker());
        }
        if (newStock.getName() != null && !newStock.getName().isBlank()) {
            existingStock.setName(newStock.getName());
        }
        if (newStock.getCurrentValue() != null) {
            existingStock.setCurrentValue(newStock.getCurrentValue());
        }
        if (newStock.getStockType() != null) {
            existingStock.setStockType(newStock.getStockType());
        }
        if (newStock.getHistoricalValues() != null && !newStock.getHistoricalValues().isEmpty()) {
            existingStock.getHistoricalValues().clear();
            existingStock.getHistoricalValues().addAll(newStock.getHistoricalValues());
        }
    }

    // Update (for /PUT method)
    public void updateStock(Long stockId, Stock newStock) {
        var stockOptional = stockRepository.findById(stockId);
        if (stockOptional.isPresent()) {
            var existingStock = stockOptional.get();
            updateModifiedFields(existingStock, newStock);
            stockRepository.save(existingStock);
        } else {
            throw new StockNotFoundException("Stock with id " + stockId + " not found");
        }
    }


}

