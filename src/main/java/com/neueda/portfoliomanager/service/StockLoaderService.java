package com.neueda.portfoliomanager.service;

import com.neueda.portfoliomanager.entity.StockType;
import com.neueda.portfoliomanager.loadingData.StockLoader;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StockLoaderService {

    private static List<String[]> loadedStocks;

    public StockLoaderService() {
        StockLoader loader = new StockLoader();
        loadedStocks = loader.loadStocks();
    }

    public List<String[]> getStocks() {
        return loadedStocks;
    }

    public static List<String> getTickers() {
        return loadedStocks.stream().map(s -> s[0]).toList(); // first column
    }

    public static List<String> getName() {
        return loadedStocks.stream().map(s -> s[1]).toList(); // second column
    }

    public static List<String> getType() {
        return loadedStocks.stream().map(s -> s[2]).toList(); // third column
    }

    public static String getNameByTicker(String ticker) {
        return loadedStocks.stream()
                .filter(s -> s[0].equalsIgnoreCase(ticker)) // filter data by ticker
                .map(s -> s[1]) // return name (second column) for this ticker
                .findFirst()
                .orElse("Name not found for: " + ticker); // set this name if ticker not found
    }

    public static StockType getTypeByTicker(String ticker) {
        return StockType.valueOf(loadedStocks.stream()
                .filter(s -> s[0].equalsIgnoreCase(ticker)) // filter data by ticker
                .map(s -> s[2]) // return type (second column) for this ticker
                .findFirst()
                .orElse("UNKNOWN")); // set this type if ticker not found
    }

}

