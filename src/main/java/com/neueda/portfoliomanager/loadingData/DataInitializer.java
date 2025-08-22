package com.neueda.portfoliomanager.loadingData;


import com.neueda.portfoliomanager.entity.StockTicker;
import com.neueda.portfoliomanager.service.StockDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@Component
public class DataInitializer implements CommandLineRunner {

    private final StockDataService stockDataService;

    public DataInitializer(StockDataService stockDataService) {
        this.stockDataService = stockDataService;
    }
    @Override
    public void run(String... args) {
        if (stockDataService.needsUpdate()) {
            List<StockTicker> tickers = Arrays.asList(StockTicker.values());

            stockDataService.fetchDataFromStooq(
                    tickers,
                    LocalDate.now().minusDays(30),
                    LocalDate.now()
            );
            System.out.println("Pobrano nowe dane z Stooq");
        }
        stockDataService.saveDataToDatabase();
        System.out.println("Dane zapisane do bazy.");
    }

}

