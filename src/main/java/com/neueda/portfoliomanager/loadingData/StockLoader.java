package com.neueda.portfoliomanager.loadingData;
import org.springframework.core.io.ClassPathResource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StockLoader {

        public List<String[]> loadStocks() {
            List<String[]> loadedStocks = new ArrayList<>();

            Path csvPath = Paths.get("data", "stocks.csv");

            try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {

                // Skip header, then split into array [ticker, name, type]
                loadedStocks = reader.lines()
                        .skip(1) // skip header row
                        .map(line -> line.split(","))
                        .map(parts -> new String[]{parts[0].trim(), parts[1].trim(), parts[2].trim()})
                        .collect(Collectors.toList());

            } catch (Exception e) {
                throw new RuntimeException("Failed to load stocks from CSV", e);
            }

            return loadedStocks;
    }
}
