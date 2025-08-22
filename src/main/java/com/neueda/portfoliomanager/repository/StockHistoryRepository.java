package com.neueda.portfoliomanager.repository;

import com.neueda.portfoliomanager.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> { }

