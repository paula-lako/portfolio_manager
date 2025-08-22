package com.neueda.portfoliomanager.model.entity;

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
    @JoinColumn(name = "user_stock_id", referencedColumnName = "id")
    private UserStock stock;

    @OneToOne
    private Portfolio portfolio;

    private double tranzactionPrice;
    private TransactionType transactionType;
    private LocalDateTime tranzactionDate;





}
