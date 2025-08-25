package com.neueda.portfoliomanager.service;

import com.neueda.portfoliomanager.entity.*;
import com.neueda.portfoliomanager.exceptions.NoExistingUserException;
import com.neueda.portfoliomanager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PortfolioService portfolioService;

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;
    private final StockRepository stockRepository;
    private final UserStockRepository userStockRepository;   


    public User getUserById(Long userId) {
        var userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new NoExistingUserException("no existing user");
        }
    }

    public List<Portfolio> getMyPortfolios(Long userId) {
        return getUserById(userId).getPortfolioList();
    }

    public User createUser(User user) {
        user.setPortfolioList(new ArrayList<>());
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void updateUser(Long userId, User newUser) {
        User userOptional = getUserById(userId);
        changeUserFields(userOptional, newUser);
        userRepository.save(userOptional);

    }

    public void changeUserFields(User modifiedUser, User newUser) {
        if (newUser.getEmail() != null) {
            modifiedUser.setEmail(newUser.getEmail());
        }
        if (newUser.getUsername() != null) {
            modifiedUser.setUsername(newUser.getUsername());
        }
    }

    public List<Portfolio> getUserPortfolios(Long userId) {
        var userOptional = getUserById(userId);
        return userOptional.getPortfolioList();
    }

    public Portfolio createUserPortfolio(Long userId, String portfolioName) {
        var userOptional = getUserById(userId);
        Portfolio portfolio = new Portfolio();
        portfolio.setTitle(portfolioName);
        portfolio.setUser(userOptional);
        portfolio.setTransactions(new ArrayList<>());
        List<Portfolio> userPortfolioList = userOptional.getPortfolioList();
        for (int i = 0; i < userPortfolioList.size(); i++) {
            Portfolio portfolio1 = userPortfolioList.get(i);
            if (portfolio1.getTitle().equals(portfolio.getTitle())) {
                userPortfolioList.set(i, portfolio1);
            }
        }
        userOptional.setPortfolioList(userPortfolioList);
        userRepository.save(userOptional);
        portfolioRepository.save(portfolio);
        return portfolio;
    }

    public Portfolio getUserPortfolio(Long userId, Long portfolioId) {
        List<Portfolio> portfolioList = portfolioRepository.findByUserId(userId);
        Optional<Portfolio> portfolioOP = portfolioList.stream().filter(p -> p.getId().equals(portfolioId)).findFirst();
        Portfolio portfolio = portfolioOP.get();

     //   Portfolio portfolio = portfolioOP.orElseThrow(() -> new RuntimeException("Portfolio not found"));
        portfolio.setValue(portfolioService.calculateCurrentValue(portfolio));
        portfolio.setReturnRate(portfolioService.calculatReturnRate(portfolio));
        portfolio.setHistory(portfolioService.calculatePortfolioHistory(portfolio));

        // obliczenia potrzebne do wyswietlenia performance,
        // dodac pola w klasie portfolio jak trzeba potem wyswietlic rozne wartosci i listy

        return portfolio;
    }

    public List<Transaction> getUserPortfolioTransactions(Long userId, Long portfolioId) {
        Portfolio portfolio = getUserPortfolio(userId, portfolioId);
        return portfolio.getTransactions();
    }

    public List<Transaction> addNewTransactionForPortfolio(Transaction transaction, Long userId, Long portfolioId, String stockTicker) {
        Portfolio portfolio = getUserPortfolio(userId, portfolioId);
        transaction.setPortfolio(portfolio);

        Optional<Stock> findStock = stockRepository.findByTicker(stockTicker);
        if (findStock.isPresent()) {
            transaction.setStock(findStock.get());
        } else {
            findStock = Optional.of(new Stock());
            transaction.setStock(findStock.get());
            //jesli nie wyszukamy stocka to trzeba dodac pola,
            // zeby wypelnic dane stocka....
        }
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTotalPrice(transaction.getUnitPrice() * transaction.getAmount());
        List<Transaction> transactionList = portfolio.getTransactions();
        transactionList.add(transaction);
        portfolio.setTransactions(transactionList);
        // znajdź UserStock w tym portfelu lub stwórz nowy
        Optional<Stock> finalFindStock = findStock;
        UserStock userStock = userStockRepository.findByPortfolioAndStock(portfolio, findStock.get())
                .orElseGet(() -> {
                    UserStock us = new UserStock();
                    us.setPortfolio(portfolio);
                    us.setStock(finalFindStock.get());
                    us.setQuantity(0);
                    us.setActualPrice(0);
                    return us;
                });

// aktualizacja liczby akcji
        if (transaction.getTransactionType() == TransactionType.BUY) {
            userStock.setQuantity(userStock.getQuantity() + transaction.getAmount());
        } else if (transaction.getTransactionType() == TransactionType.SELL) {
            if (userStock.getQuantity() < transaction.getAmount()) {
                throw new RuntimeException("Not enough stocks to sell");
            }
            userStock.setQuantity(userStock.getQuantity() - transaction.getAmount());
        }
        userStock.setActualPrice(userStock.getQuantity() * findStock.get().getCurrentValue());
        userStockRepository.save(userStock);
        portfolioRepository.save(portfolio);
        transactionRepository.save(transaction);
        User user = getUserById(userId);
        List<Portfolio> userPortfolioList = user.getPortfolioList();
        for (int i = 0; i < userPortfolioList.size(); i++) {
            Portfolio portfolio1 = userPortfolioList.get(i);
            if (portfolio1.getTitle().equals(portfolio.getTitle())) {
                userPortfolioList.set(i, portfolio1);
            }
        }
        user.setPortfolioList(userPortfolioList);
        userRepository.save(user);
        return portfolio.getTransactions();
    }
}