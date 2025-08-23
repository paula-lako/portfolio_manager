package com.neueda.portfoliomanager.controllers;

import com.neueda.portfoliomanager.entity.Stock;
import com.neueda.portfoliomanager.repository.StockRepository;
import com.neueda.portfoliomanager.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    private final Logger logger = LoggerFactory.getLogger(StockController.class);

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<Stock>> getBooks() {
        logger.info("GET /stocks called");
        List<Stock> stocks = stockService.getAllStocks();
        for (Stock stock : stocks) {
            logger.info("Book details: {}", stock);
        }
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        logger.info("GET /stocks/{} called", id);
        Stock stocks = stockService.getStockById(id);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/ticker/{ticker}")
    public ResponseEntity<Stock> getStockById(@PathVariable String ticker) {
        logger.info("GET /stocks/ticker/{} called", ticker);
        Stock stocks = stockService.getStockByTicker(ticker);
        return ResponseEntity.ok(stocks);
    }

    @PostMapping
    public ResponseEntity<Stock> createStock(@RequestBody Stock stock) {
        Stock createdStock = stockService.createStock(stock);
        return new ResponseEntity<>(createdStock, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{stockId}")
    public ResponseEntity<String> updateStock(@PathVariable Long stockId, @RequestBody Stock stock) {
        stockService.updateStock(stockId, stock);
        return ResponseEntity.ok("Stock successfully updated!");
    }

}