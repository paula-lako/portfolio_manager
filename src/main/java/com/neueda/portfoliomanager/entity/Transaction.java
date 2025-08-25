package com.neueda.portfoliomanager.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", referencedColumnName = "id")
    @JsonBackReference("portfolio-transactions")
    private Portfolio portfolio;

    private double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime transactionDate;
    private double totalPrice;  // amount * unitPrice
    private double unitPrice;

    public Transaction(Long id, double amount, TransactionType transactionType, double unitPrice) {
        this.id = id;
        this.amount = amount;
        this.transactionType = transactionType;
        this.unitPrice = unitPrice;
    }
}
