package com.neueda.portfoliomanager.controllers;

import com.neueda.portfoliomanager.entity.Transaction;
import com.neueda.portfoliomanager.entity.TransactionType;
import com.neueda.portfoliomanager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // Dodanie nowej transakcji (kupno/sprzedaż)
    @PostMapping("/portfolio/{portfolioId}/stock/{stockId}")
    public ResponseEntity<Transaction> addTransaction(
            @PathVariable Long portfolioId,
            @PathVariable Long stockId,
            @RequestParam TransactionType type,
            @RequestParam double amount,      // ✅ fractional shares
            @RequestParam double unitPrice) {

        Transaction transaction = transactionService.addTransaction(portfolioId, stockId, type, amount, unitPrice);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    // Pobranie wszystkich transakcji w portfelu
    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<List<Transaction>> getTransactionsByPortfolio(@PathVariable Long portfolioId) {
        return ResponseEntity.ok(transactionService.getTransactionsByPortfolio(portfolioId));
    }
}

