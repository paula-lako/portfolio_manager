package com.neueda.portfoliomanager.repository;

import com.neueda.portfoliomanager.entity.Portfolio;
import com.neueda.portfoliomanager.entity.Stock;
import com.neueda.portfoliomanager.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByPortfolioId(Long portfolioId);

    @Query("SELECT SUM(CASE WHEN t.transactionType = 'BUY' THEN t.amount ELSE -t.amount END) " +
            "FROM Transaction t WHERE t.portfolio.id = :portfolioId AND t.stock.id = :stockId")
    Double calculateStockQuantity(@Param("portfolioId") Long portfolioId, @Param("stockId") Long stockId);

    List<Transaction> findByPortfolioAndStock(Portfolio portfolio1, Stock stock);
}
