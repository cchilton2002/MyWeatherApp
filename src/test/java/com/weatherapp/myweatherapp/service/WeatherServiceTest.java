package com.weatherapp.myweatherapp.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    public void setUp() {
        // Inject the API key using ReflectionTestUtils
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-api-key");
    }

    @Test
    public void testGetWeatherData_Success() throws Exception {
        // Mock API response
        String mockResponse = "{ \"days\": [ { \"sunrise\": \"06:00:00\", \"sunset\": \"18:00:00\", \"conditions\": \"Rain\" } ] }";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        // Call the method
        JsonNode result = weatherService.getWeatherData("London");

        // Verify the result
        assertNotNull(result);
        assertEquals("06:00:00", result.findPath("days").get(0).findPath("sunrise").asText());
        assertEquals("Rain", result.findPath("days").get(0).findPath("conditions").asText());

        // Verify that the API was called
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    public void testGetWeatherData_Failure() {
        // Simulate an exception when calling the API
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(new RuntimeException("API Error"));

        // Verify that the exception is thrown
        Exception exception = assertThrows(RuntimeException.class, () -> {
            weatherService.getWeatherData("London");
        });
        assertEquals("Error fetching weather data for London: API Error", exception.getMessage());
    }

    @Test
    public void testCompareDaylightHours_City1Longer() throws Exception {
        // Mock API responses for two cities
        String mockResponse1 = "{ \"days\": [ { \"sunrise\": \"06:00:00\", \"sunset\": \"18:00:00\" } ] }";
        String mockResponse2 = "{ \"days\": [ { \"sunrise\": \"07:00:00\", \"sunset\": \"17:00:00\" } ] }";
        when(restTemplate.getForObject(contains("London"), eq(String.class))).thenReturn(mockResponse1);
        when(restTemplate.getForObject(contains("Paris"), eq(String.class))).thenReturn(mockResponse2);

        // Call the method
        String result = weatherService.compareDaylightHours("London", "Paris");

        // Verify the result
        assertEquals("London has longer daylight hours than Paris.", result);
    }

    @Test
    public void testRainCheck_BothRaining() throws Exception {
        // Mock API responses for two cities
        String mockResponse1 = "{ \"days\": [ { \"conditions\": \"Rain\" } ] }";
        String mockResponse2 = "{ \"days\": [ { \"conditions\": \"Drizzle\" } ] }";
        when(restTemplate.getForObject(contains("London"), eq(String.class))).thenReturn(mockResponse1);
        when(restTemplate.getForObject(contains("Paris"), eq(String.class))).thenReturn(mockResponse2);

        // Call the method
        String result = weatherService.rainCheck("London", "Paris");

        // Verify the result
        assertEquals("Both London and Paris are raining.", result);
    }

    @Test
    public void testRainCheck_NeitherRaining() throws Exception {
        // Mock API responses for two cities
        String mockResponse1 = "{ \"days\": [ { \"conditions\": \"Clear\" } ] }";
        String mockResponse2 = "{ \"days\": [ { \"conditions\": \"Sunny\" } ] }";
        when(restTemplate.getForObject(contains("London"), eq(String.class))).thenReturn(mockResponse1);
        when(restTemplate.getForObject(contains("Paris"), eq(String.class))).thenReturn(mockResponse2);

        // Call the method
        String result = weatherService.rainCheck("London", "Paris");

        // Verify the result
        assertEquals("Neither London nor Paris is raining.", result);
    }
}