package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Use RestController for direct response body
@RequestMapping("/weather") // Base path for your endpoints
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/daylight")
    public ResponseEntity<String> compareDaylight(@RequestParam String city1, @RequestParam String city2) {
        try {
            String result = weatherService.compareDaylightHours(city1, city2);
            return ResponseEntity.ok(result); // Return 200 OK with the result
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Return 400 Bad Request with error
        }
    }

    @GetMapping("/rain")
    public ResponseEntity<String> rainCheck(@RequestParam String city1, @RequestParam String city2) {
        try {
            String result = weatherService.rainCheck(city1, city2); // Call the new method
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}