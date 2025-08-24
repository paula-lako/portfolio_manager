package com.neueda.portfoliomanager.repository;

import com.neueda.portfoliomanager.entity.Portfolio;
import com.neueda.portfoliomanager.entity.Stock;
import com.neueda.portfoliomanager.entity.UserStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserStockRepository extends JpaRepository<UserStock, Long> {

    Optional<UserStock> findByPortfolioAndStock(Portfolio portfolio, Stock stock);
    List<UserStock> findByPortfolioId(Long portfolioId);
}
