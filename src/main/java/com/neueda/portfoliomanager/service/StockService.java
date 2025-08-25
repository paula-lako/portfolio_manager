package com.neueda.portfoliomanager.service;

import com.neueda.portfoliomanager.entity.Stock;
import com.neueda.portfoliomanager.exceptions.InvalidStockException;
import com.neueda.portfoliomanager.exceptions.StockNotFoundException;
import com.neueda.portfoliomanager.entity.StockHistory;
import com.neueda.portfoliomanager.repository.StockHistoryRepository;
import com.neueda.portfoliomanager.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final StockDataService stockDataService;

    @Autowired
    public StockService(StockRepository stockRepository, StockHistoryRepository stockHistoryRepository, StockDataService stockDataService) {
        this.stockRepository = stockRepository;
        this.stockHistoryRepository = stockHistoryRepository;
        this.stockDataService = stockDataService;
    }

    private void appendStockToCsv(Stock stock) {
        try {

            Path path = Paths.get("src/main/resources/data/stocks.csv");

            // Row format: ticker,name,stockType
            String row = stock.getTicker() + "," + stock.getName() + "," + stock.getStockType() + System.lineSeparator();

            Files.write(path, row.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Failed to append stock to CSV", e);
        }
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
    }

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
        if (stock.getCurrentValue() == 0) {
            throw new InvalidStockException("Stock current value cannot be empty");
        }

        if (stockRepository.existsByTicker(stock.getTicker())) {
            throw new InvalidStockException(
                    "Stock with ticker '" + stock.getTicker() + "' already exists"
            );
        }

        //Add row to the stock file
        appendStockToCsv(stock);

        //Try to download historical data for this ticker and save the ticker to repository
        return stockDataService.fetchAndSaveSingleTicker(stock.getTicker(), stock.getName(), stock.getStockType(), LocalDate.now().minusDays(30), LocalDate.now());
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
        if (newStock.getCurrentValue() != 0) {
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

