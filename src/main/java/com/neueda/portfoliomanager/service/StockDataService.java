package com.neueda.portfoliomanager.service;


import com.neueda.portfoliomanager.entity.Stock;
import com.neueda.portfoliomanager.entity.StockHistory;
import com.neueda.portfoliomanager.entity.StockTicker;
import com.neueda.portfoliomanager.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockDataService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private final StockRepository stockRepository;

    public StockDataService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public boolean needsUpdate() {
        File folder = new File("stocks");
        if (!folder.exists()) return true;

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null || files.length == 0) return true;

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String lastLine = null, line;
                while ((line = reader.readLine()) != null) {
                    lastLine = line;
                }
                if (lastLine != null) {
                    String[] parts = lastLine.split(",");
                    LocalDate lastDate = LocalDate.parse(parts[0]);
                    if (lastDate.isBefore(LocalDate.now().minusDays(1))) {
                        return true; // stare dane
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }
        return false; // dane są świeże
    }

    /**
     * Pobiera dane dla listy tickerów i zapisuje je do plików w folderze stocks/
     */
    public void fetchDataFromStooq(List<StockTicker> tickers, LocalDate startDate, LocalDate endDate) {
        File folder = new File("stocks");
        if (!folder.exists()) folder.mkdirs();

        for (StockTicker tickerEnum  : tickers) {
            String ticker = tickerEnum.getTicker();
            String url = String.format(
                    "https://stooq.com/q/d/l/?s=%s&i=d&d1=%s&d2=%s",
                    ticker.toLowerCase(),
                    startDate.format(FORMATTER),
                    endDate.format(FORMATTER)
            );
            File outFile = new File(folder, ticker.toUpperCase() + ".csv");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Ładuje dane z plików CSV w folderze stocks i zapisuje do bazy danych
     */
    @Transactional
    public void saveDataToDatabase() {
        File folder = new File("stocks");
        if (!folder.exists()) return;

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null) return;

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String tickerString = file.getName().replace(".csv", "").toUpperCase();
                StockTicker tickerEnum = StockTicker.valueOf(tickerString);
                Stock stock = stockRepository.findByTicker(tickerEnum)
                        .orElseGet(() -> {
                            Stock s = new Stock();
                            s.setTicker(tickerEnum);
                            s.setName("Company named " + tickerEnum);
                            return s;
                        });

                List<StockHistory> historyList = new ArrayList<>();
                String line;
                boolean skipHeader = true;

                while ((line = reader.readLine()) != null) {
                    if (skipHeader) {
                        skipHeader = false;
                        continue;
                    }
                    String[] parts = line.split(",");
                    if (parts.length < 5) continue;

                    LocalDate date = LocalDate.parse(parts[0]);
                    Double closeValue = Double.parseDouble(parts[4]);

                    StockHistory history = new StockHistory();
                    history.setDate(date);
                    history.setCloseValue(closeValue);
                    history.setStock(stock);
                    historyList.add(history);
                }

                if (!historyList.isEmpty()) {
                    stock.setCurrentValue(historyList.get(historyList.size() - 1).getCloseValue());
                }

                stock.getHistoricalValues().clear();
                stock.getHistoricalValues().addAll(historyList);

                stockRepository.save(stock);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
