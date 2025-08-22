package com.neueda.portfoliomanager.loadingData;
import com.neueda.portfoliomanager.service.StockDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class DataLoader implements CommandLineRunner {

    private final StockDataService stockDataService;

    public DataLoader(StockDataService stockDataService) {
        this.stockDataService = stockDataService;
    }

    @Override
    public void run(String... args) throws Exception {
        stockDataService.saveDataToDatabase();
    }
}