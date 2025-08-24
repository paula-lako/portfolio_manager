package com.neueda.portfoliomanager.exceptions;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String message) {
        super(message);
    }
}
