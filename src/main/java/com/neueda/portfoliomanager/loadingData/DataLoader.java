package com.neueda.portfoliomanager.loadingData;

import com.neueda.portfoliomanager.entity.Portfolio;
import com.neueda.portfoliomanager.entity.User;
import com.neueda.portfoliomanager.repository.PortfolioRepository;
import com.neueda.portfoliomanager.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;

    public void findUser() {
        if (userRepository.existsById(1L)) {
            userRepository.findById(1L).get();
        } else {
            User user = new User();
            Portfolio portfolio = new Portfolio();
            portfolio.setUser(user);
            portfolio.setTitle("Portfolio");
            portfolio.setTransactions(new ArrayList<>());
            portfolio.setUserStocksList(new ArrayList<>());
            List<Portfolio> portfolioList = new ArrayList<>();
            portfolioList.add(portfolio);
            user.setPortfolioList(portfolioList);
            userRepository.save(user);
            portfolioRepository.save(portfolio);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        findUser();
    }
}
