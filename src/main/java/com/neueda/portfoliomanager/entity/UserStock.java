package com.neueda.portfoliomanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;


    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"user", "transactions"})
    @JsonIgnore
    private Portfolio portfolio;

    private double actualPrice; // quantity * stock.currentValue
    private double quantity;
}
