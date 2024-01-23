package com.example.demoavia;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightAnalyzer {
    public static void main(String[] args) {
        try {// Чтение файла tickets.json
            File file = new File("files/tickets.json");
            String fileContent = new String(Files.readAllBytes(file.toPath()));
            JSONArray tickets = new JSONArray(fileContent);
            // Рассчет минимального времени полета между городами Владивосток и Тель-Авив для каждого авиаперевозчика
            Map<String, Integer> minFlightTimes = new HashMap<>();
            for (int i = 0; i < tickets.length(); i++) {
                JSONObject ticket = tickets.getJSONObject(i);
                String origin = ticket.getString("origin");
                String destination = ticket.getString("destination");
                String carrier = ticket.getString("carrier");
                if (origin.equals("VVO") && destination.equals("TLV")) {
                    int flightTime = calculateFlightTime(ticket.getString("departure_date"),
                            ticket.getString("departure_time"), ticket.getString("arrival_date"),
                            ticket.getString("arrival_time"));
                    if (!minFlightTimes.containsKey(carrier) || flightTime < minFlightTimes.get(carrier)) {
                        minFlightTimes.put(carrier, flightTime);
                    }
                }
            }
            System.out.println(
                    "Минимальное время полета между городами Владивосток и Тель-Авив для каждого авиаперевозчика:");
            for (Map.Entry<String, Integer> entry : minFlightTimes.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + " минут");
            }
            // Рассчет разницы между средней ценой и медианой для полета между городами Владивосток и Тель-Авив
            List<Integer> prices = new ArrayList<>();
            for (int i = 0; i < tickets.length(); i++) {
                JSONObject ticket = tickets.getJSONObject(i);
                String origin = ticket.getString("origin");
                String destination = ticket.getString("destination");
                if (origin.equals("VVO") && destination.equals("TLV")) {
                    int price = ticket.getInt("price");
                    prices.add(price);
                }
            }
            double averagePrice = prices.stream().mapToInt(Integer::intValue).average().getAsDouble();
            double medianPrice;
            Collections.sort(prices);
            int middle = prices.size() / 2;
            if (prices.size() % 2 == 0) {
                medianPrice = (prices.get(middle - 1) + prices.get(middle)) / 2.0;
            } else {
                medianPrice = prices.get(middle);
            }
            double difference = averagePrice - medianPrice;
            System.out.println(
                    "Разница между средней ценой и медианой для полета между городами Владивосток и Тель-Авив: "
                            + difference);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int calculateFlightTime(String departureDate, String departureTime, String arrivalDate,
            String arrivalTime) {
        LocalDateTime departureDateTime = LocalDateTime.parse(departureDate + "T" + departureTime,
                DateTimeFormatter.ofPattern("dd.MM.yy'T'H:mm"));
        LocalDateTime arrivalDateTime = LocalDateTime.parse(arrivalDate + "T" + arrivalTime,
                DateTimeFormatter.ofPattern("dd.MM.yy'T'H:mm"));
        Duration duration = Duration.between(departureDateTime, arrivalDateTime);
        return (int) duration.toMinutes();
    }
}