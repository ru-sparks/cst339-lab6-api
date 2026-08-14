package com.sparkco.lab2_api.features.greeting;

public class GreetingResponse {

    private final String message;
    private final int year;

    public GreetingResponse(String message, int year) {
        this.message = message;
        this.year = year;
    }

    public String getMessage() {
        return message;
    }

    public int getYear() {
        return year;
    }
}
