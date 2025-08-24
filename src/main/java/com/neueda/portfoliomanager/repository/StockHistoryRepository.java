package com.neueda.portfoliomanager.repository;

import com.neueda.portfoliomanager.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    Optional<StockHistory> findTopByStockIdOrderByDateDesc(Long stockId);
}

