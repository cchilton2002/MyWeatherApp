package com.weatherapp.myweatherapp.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.weatherapp.myweatherapp.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
    }

    @Test
    public void testCompareDaylight_Success() throws Exception {
        // Mock the service response
        when(weatherService.compareDaylightHours("London", "Paris")).thenReturn("London has longer daylight hours than Paris.");

        // Perform the request and verify the response
        mockMvc.perform(get("/weather/daylight")
                .param("city1", "London")
                .param("city2", "Paris"))
                .andExpect(status().isOk())
                .andExpect(content().string("London has longer daylight hours than Paris."));

        // Verify that the service was called
        verify(weatherService, times(1)).compareDaylightHours("London", "Paris");
    }

    @Test
    public void testCompareDaylight_Failure() throws Exception {
        // Mock the service to throw an exception
        when(weatherService.compareDaylightHours("London", "Paris")).thenThrow(new RuntimeException("Error comparing daylight hours"));

        // Perform the request and verify the error response
        mockMvc.perform(get("/weather/daylight")
                .param("city1", "London")
                .param("city2", "Paris"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error comparing daylight hours"));

        // Verify that the service was called
        verify(weatherService, times(1)).compareDaylightHours("London", "Paris");
    }

    @Test
    public void testRainCheck_Success() throws Exception {
        // Mock the service response
        when(weatherService.rainCheck("London", "Paris")).thenReturn("Both London and Paris are raining.");

        // Perform the request and verify the response
        mockMvc.perform(get("/weather/rain")
                .param("city1", "London")
                .param("city2", "Paris"))
                .andExpect(status().isOk())
                .andExpect(content().string("Both London and Paris are raining."));

        // Verify that the service was called
        verify(weatherService, times(1)).rainCheck("London", "Paris");
    }

    @Test
    public void testRainCheck_Failure() throws Exception {
        // Mock the service to throw an exception
        when(weatherService.rainCheck("London", "Paris")).thenThrow(new RuntimeException("Error checking rain"));

        // Perform the request and verify the error response
        mockMvc.perform(get("/weather/rain")
                .param("city1", "London")
                .param("city2", "Paris"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error checking rain"));

        // Verify that the service was called
        verify(weatherService, times(1)).rainCheck("London", "Paris");
    }
}