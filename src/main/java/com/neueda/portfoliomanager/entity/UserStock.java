package com.neueda.portfoliomanager.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonIgnoreProperties({"historicalValues"})
    private Stock stock;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    @JsonBackReference("portfolio-stocks")
    private Portfolio portfolio;

    private double actualPrice; // quantity * stock.currentValue
    private double quantity;
    @Transient
    private double investedMoney;



    //historia... z stock history

}
