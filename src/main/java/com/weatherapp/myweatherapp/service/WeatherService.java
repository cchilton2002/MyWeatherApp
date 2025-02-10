package com.weatherapp.myweatherapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    @Value("${weather.visualcrossing.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String VISUAL_CROSSING_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";

    public JsonNode getWeatherData(String city) {
        String url = VISUAL_CROSSING_URL + city + "?unitGroup=metric&key=" + apiKey + "&contentType=json";
        try {
            String response = restTemplate.getForObject(url, String.class);
            return objectMapper.readTree(response);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching weather data for " + city + ": " + e.getMessage());
        }
    }

    public String compareDaylightHours(String city1, String city2) {
        try {
            JsonNode data1 = getWeatherData(city1);
            JsonNode data2 = getWeatherData(city2);

            String sunrise1 = data1.findPath("days").get(0).findPath("sunrise").asText();
            String sunset1 = data1.findPath("days").get(0).findPath("sunset").asText();
            String sunrise2 = data2.findPath("days").get(0).findPath("sunrise").asText();
            String sunset2 = data2.findPath("days").get(0).findPath("sunset").asText();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // Adjust format if needed

            LocalTime startTime1 = LocalTime.parse(sunrise1.substring(0, 8), formatter);
            LocalTime endTime1 = LocalTime.parse(sunset1.substring(0, 8), formatter);
            LocalTime startTime2 = LocalTime.parse(sunrise2.substring(0, 8), formatter);
            LocalTime endTime2 = LocalTime.parse(sunset2.substring(0, 8), formatter);

            long daylight1 = ChronoUnit.MINUTES.between(startTime1, endTime1);
            long daylight2 = ChronoUnit.MINUTES.between(startTime2, endTime2);

            if (daylight1 > daylight2) {
                return city1 + " has longer daylight hours than " + city2 + ".";
            } else if (daylight2 > daylight1) {
                return city2 + " has longer daylight hours than " + city1 + ".";
            } else {
                return "Both cities have the same daylight hours.";
            }
        } catch (Exception e) {
            throw new RuntimeException("Error comparing daylight hours: " + e.getMessage());
        }
    }

    public String rainCheck(String city1, String city2) {
        try {
            boolean raining1 = isRainingInCity(city1);
            boolean raining2 = isRainingInCity(city2);

            if (raining1 && raining2) {
                return "Both " + city1 + " and " + city2 + " are raining.";
            } else if (raining1) {
                return city1 + " is raining, " + city2 + " is not raining.";
            } else if (raining2) {
                return city1 + " is not raining, " + city2 + " is raining.";
            } else {
                return "Neither " + city1 + " nor " + city2 + " is raining.";
            }
        } catch (Exception e) {
            logger.error("Error checking rain: " + e.getMessage());
            throw new RuntimeException("Error checking rain: " + e.getMessage());
        }
    }

    // Helper method to check rain in a single city
    private boolean isRainingInCity(String city) {
        try {
            JsonNode data = getWeatherData(city);
            String conditions = data.findPath("days").get(0).findPath("conditions").asText().toLowerCase();
            logger.info("Conditions for {}: {}", city, conditions);
            return conditions.contains("rain") || conditions.contains("drizzle") || conditions.contains("sleet") || conditions.contains("showers");
        } catch (Exception e) {
            logger.error("Error checking rain for " + city + ": " + e.getMessage());
            throw new RuntimeException("Error checking rain for " + city + ": " + e.getMessage());
        }
    }
}