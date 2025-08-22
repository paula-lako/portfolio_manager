package com.neueda.portfoliomanager.repository;

import com.neueda.portfoliomanager.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByPortfolioId(Long portfolioId);
    List<Transaction> findByStockId(Long stockId);
}
