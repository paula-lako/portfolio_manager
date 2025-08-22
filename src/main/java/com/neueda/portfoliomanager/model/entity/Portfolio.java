package com.neueda.portfoliomanager.model.entity;

import jakarta.persistence.*;
import jakarta.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {
    @Id
    private Long id;

    @OneToOne
    private User user;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Transaction> transactions;
}
