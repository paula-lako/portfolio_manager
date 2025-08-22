package com.neueda.portfoliomanager.service;


import com.neueda.portfoliomanager.entity.Transaction;
import com.neueda.portfoliomanager.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Transaction saveTransaction(Transaction transaction) {
        transaction.setTotalPrice(transaction.getAmount() * transaction.getUnitPrice());
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionsByPortfolio(Long portfolioId) {
        return transactionRepository.findByPortfolioId(portfolioId);
    }
}