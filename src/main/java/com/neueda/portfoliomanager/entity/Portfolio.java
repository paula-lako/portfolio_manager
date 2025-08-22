package com.neueda.portfoliomanager.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @OneToMany(mappedBy ="portfolio_id", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Transaction> transactions;
}
