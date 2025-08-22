package com.neueda.portfoliomanager.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String fullName;
    private String letterCode;
    private StockType stockType;
    private double actualPrice;
    @OneToMany(cascade = CascadeType.ALL)
    private List<StockHistory> stockHistory;






}
