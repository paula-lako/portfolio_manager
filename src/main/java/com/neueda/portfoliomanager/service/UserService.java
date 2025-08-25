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

        // obliczenia potrzebne do wyswietlenia performance,
        // dodac pola w klasie portfolio jak trzeba potem wyswietlic rozne wartosci i listy
        // uzyj user stock

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
        transaction.setStock(findStock.get());

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
        userStockRepository.save(userStock);
        portfolioRepository.save(portfolio);
        transactionRepository.save(transaction);
        return portfolio.getTransactions();
    }

    public List<UserStock> getUserStocks(Long userId, Long portfolioId) {
        User user = getUserById(userId);
        List<Portfolio> portfolioList = user.getPortfolioList();
        List<UserStock> userStocksList = new ArrayList<>();

        for (int i = 0; i < portfolioList.size(); i++) {
            Portfolio portfolio1 = portfolioList.get(i);
            if (portfolio1.getId().equals(portfolioId)) {
                userStocksList = portfolio1.getUserStocksList();

                for (UserStock us : userStocksList) {
                    List<Transaction> transactions =
                            transactionRepository.findByPortfolioAndStock(portfolio1, us.getStock());

                    double invested = 0;
                    double quantity = 0;

                    for (Transaction t : transactions) {
                        if (t.getTransactionType() == TransactionType.BUY) {
                            invested += t.getUnitPrice() * t.getAmount();
                            quantity += t.getAmount();
                        } else if (t.getTransactionType() == TransactionType.SELL) {
                            double avgPrice = invested / (quantity == 0 ? 1 : quantity);
                            invested -= avgPrice * t.getAmount();
                            quantity -= t.getAmount();
                        }
                    }

                    us.setInvestedMoney(invested);
                    us.setQuantity(quantity);
                    us.setActualPrice(quantity * us.getStock().getCurrentValue());
                }
                return userStocksList;
            }
        }

        throw new RuntimeException("Portfolio id not found");
    }


    public Transaction updateTransaction(Long userId, Long portfolioId, Long transactionId, Transaction updatedTransaction) {
        Transaction existing = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        existing.setAmount(updatedTransaction.getAmount());
        existing.setUnitPrice(updatedTransaction.getUnitPrice());
        existing.setTransactionType(updatedTransaction.getTransactionType());
        existing.setTransactionDate(updatedTransaction.getTransactionDate());


        return transactionRepository.save(existing);
    }

    public void deleteTransaction(Long userId, Long portfolioId, Long transactionId) {
        Transaction existing = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transactionRepository.delete(existing);
    }



}