package com.neueda.portfoliomanager.controllers;

import com.neueda.portfoliomanager.entity.Portfolio;
import com.neueda.portfoliomanager.entity.Transaction;
import com.neueda.portfoliomanager.entity.User;
import com.neueda.portfoliomanager.entity.UserStock;
import com.neueda.portfoliomanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user) {
        userService.updateUser(userId, user);
        return ResponseEntity.ok("User successfully updated!");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") Long userId) {
        User user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{userId}/portfolios")
    public ResponseEntity<List<Portfolio>> getUserPortfolios(@PathVariable("userId") Long userId) {
        List<Portfolio> userPortfolios = userService.getUserPortfolios(userId);
        return new ResponseEntity<>(userPortfolios, HttpStatus.OK);
    }

    @PostMapping("/{userId}/portfolios/create")
    public ResponseEntity<Portfolio> createNewPortfolio(@PathVariable("userId") Long userId, @RequestParam String portfolioName) {
        return new ResponseEntity<>(userService.createUserPortfolio(userId, portfolioName), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/portfolios/{portfolioId}")
    public ResponseEntity<Portfolio> displayPortfolioPerformance(@PathVariable("userId") Long userId, @PathVariable("portfolioId") Long portfolioId) {
        return new ResponseEntity<>(userService.getUserPortfolio(userId, portfolioId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/portfolios/{portfolioId}/managePortfolio")
    public ResponseEntity<List<Transaction>> managePortfolio(@PathVariable("userId") Long userId, @PathVariable("portfolioId") Long portfolioId) {
        return new ResponseEntity<>(userService.getUserPortfolioTransactions(userId, portfolioId), HttpStatus.OK);
    }
    @GetMapping("/{userId}/portfolios/{portfolioId}/stocks")
    public ResponseEntity<List<UserStock>> getUserStocks(@PathVariable("userId") Long userId, @PathVariable("portfolioId") Long portfolioId) {
        List<UserStock> userStocks = userService.getUserStocks(userId, portfolioId);
        return new ResponseEntity<>(userStocks, HttpStatus.OK);
    }

    @PostMapping("/{userId}/portfolios/{portfolioId}/managePortfolio/newTransaction")
    public ResponseEntity<List<Transaction>> addNewTransactionForPortfolio(@RequestParam String stockTicker, @RequestBody Transaction transaction, @PathVariable("userId") Long userId, @PathVariable("portfolioId") Long portfolioId) {
        return new ResponseEntity<>(userService.addNewTransactionForPortfolio(transaction, userId, portfolioId, stockTicker), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/portfolios/{portfolioId}/transactions/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long userId, @PathVariable Long portfolioId, @PathVariable Long transactionId,@RequestBody Transaction updatedTransaction) {
        return new ResponseEntity<>(userService.updateTransaction(userId, portfolioId, transactionId, updatedTransaction), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/portfolios/{portfolioId}/transactions/{transactionId}")
    public void deleteTransaction( @PathVariable Long userId, @PathVariable Long portfolioId, @PathVariable Long transactionId) {
        userService.deleteTransaction(userId, portfolioId, transactionId);
    }

}