package com.group2.glamping.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CurrencyConverter {

    public static double convertVndToUsd(double amountVnd, String apiKey) throws IOException {
        String urlString = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/VND";

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
        }

        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

        if (!jsonResponse.get("result").getAsString().equals("success")) {
            String errorType = jsonResponse.get("error-type").getAsString();
            throw new IOException("Failed to fetch exchange rate: " + errorType);
        }

        double exchangeRate = jsonResponse.getAsJsonObject("conversion_rates").get("USD").getAsDouble();
        System.out.println("Exchange rate (1 VND to USD): " + exchangeRate);

        double amountUsd = amountVnd * exchangeRate;
        System.out.println("Converted amount (USD): " + amountUsd);
        return amountUsd;
    }


}