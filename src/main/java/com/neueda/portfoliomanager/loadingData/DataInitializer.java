package com.neueda.portfoliomanager.loadingData;


import com.neueda.portfoliomanager.service.StockDataService;
import com.neueda.portfoliomanager.service.StockLoaderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.management.openmbean.CompositeData;
import java.time.LocalDate;
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
            stockDataService.fetchDataFromStooq(
                    StockLoaderService.getTickers(),
                    LocalDate.now().minusDays(30),
                    LocalDate.now());
            System.out.println("Pobrano nowe dane z Stooq");
        }
        stockDataService.saveDataToDatabase();
        System.out.println("Dane zapisane do bazy.");
    }

}

