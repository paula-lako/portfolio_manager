package com.neueda.portfoliomanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<UserStock> userStocksList;
    // dodaj liste userStocks
}
