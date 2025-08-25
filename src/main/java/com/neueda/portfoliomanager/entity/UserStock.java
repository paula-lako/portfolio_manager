package com.neueda.portfoliomanager.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private Stock stock;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", referencedColumnName = "id")
    @JsonBackReference
    private Portfolio portfolio;

    private double actualPrice; // quantity * stock.currentValue
    private double quantity;

    //historia... z stock history

}
