package com.group2.glamping.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CurrencyConverter {

    public static double convertVndToUsd(double amountVnd, String apiKey) throws IOException {
        // API endpoint
        String urlString = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/VND";

        // Make the API request
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Read the response
        StringBuilder response = new StringBuilder();
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
        }

        // Parse the JSON response
        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

        // Check if the request was successful
        if (!jsonResponse.get("result").getAsString().equals("success")) {
            String errorType = jsonResponse.get("error-type").getAsString();
            throw new IOException("Failed to fetch exchange rate: " + errorType);
        }

        // Get the exchange rate for USD
        double exchangeRate = jsonResponse.getAsJsonObject("conversion_rates").get("USD").getAsDouble();
        System.out.println("Exchange rate (1 VND to USD): " + exchangeRate);

        // Convert VND to USD
        double amountUsd = amountVnd * exchangeRate;
        System.out.println("Converted amount (USD): " + amountUsd);
        return amountUsd;
    }


}