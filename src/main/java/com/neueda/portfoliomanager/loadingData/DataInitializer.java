package com.neueda.portfoliomanager.loadingData;


import com.neueda.portfoliomanager.service.StockDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Component
public class DataInitializer implements CommandLineRunner {

    private final StockDataService stockDataService;

    public DataInitializer(StockDataService stockDataService) {
        this.stockDataService = stockDataService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> tickers = List.of(
                "PKO","PKN","PZU","SPL","PEO","DNP","ING","MBK","ALE","LPP",
                "KGH","PGE","CDR","ZAB","ACP","ALR","TPE","BHW","BDX","CCC",
                "PCO","OPL","ENA","CPS","XTB","KTY","KRU","MRB","CAR","ASE",
                "GPW","WPL","RBW","NEU","MIL","PEP","PKP","APR","STP","LWB",
                "BFT","DOM","ERB","ENT","UNI","ANR","SNK"
        );

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(30);

        stockDataService.fetchDataFromStooq(tickers, start, end);

        System.out.println("Dane wczytane z pliku.");


        stockDataService.saveDataToDatabase();
        System.out.println("Dane zapisane do bazy.");
    }
}

