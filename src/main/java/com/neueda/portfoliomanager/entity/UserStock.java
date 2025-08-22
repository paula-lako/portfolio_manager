package com.neueda.portfoliomanager.entity;

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

    @OneToOne(cascade = CascadeType.ALL)
    private Stock stock;

    @ManyToOne(cascade = CascadeType.ALL)
    private Portfolio portfolio;

    private double actualPrice; // quantity * stock.currentValue
    private int quantity;
}
