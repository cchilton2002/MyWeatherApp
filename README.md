# Weather Application

A simple Spring Boot application that provides weather information, including comparisons between cities.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Build and Run](#build-and-run)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)



## Features

This application provides the following features through two API endpoints:

Daylight Comparison:

    - Compare the length of daylight hours between two cities.

    - Returns a message indicating which city has longer daylight hours or if they are equal.

Rain Check:

    - Check if it's currently raining in one or two cities.

    - Returns a message indicating the rain status for the specified cities.

The application uses the Visual Crossing Weather API to fetch real-time weather data, including sunrise, sunset, and current weather conditions.

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- Git (optional, but recommended for version control)
- A Visual Crossing Weather API key (see [API Key](#api-key) section)

## Build and Run

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/cchilton2002/MyWeatherApp.git 
    cd MyWeatherApp
    ```

2.  **Add your API key:**
    Create a file named `application.properties` in the `src/main/resources` directory and add your Visual Crossing API key:

    ```properties
    weather.visualcrossing.key=<YOUR_API_KEY>
    ```

3.  **Build the application:**
    ```bash
    mvn clean install
    ```

4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```

## API Endpoints

The following endpoints have been added:

-   **Compare Daylight Hours:**
    ```
    GET /weather/daylight?city1={city1}&city2={city2}
    ```
    -   `city1`: Name of the first city.
    -   `city2`: Name of the second city.
    -   Returns: A string indicating which city has longer daylight hours or if they are the same.

-   **Rain Check:**
    ```
    GET /weather/rain?city1={city1}&city2={city2}
    ```
    -   `city1`: Name of the first city.
    -   `city2`: Name of the second city.
    -   Returns: A string indicating which city is raining, if both are, or if neither is.

## Testing

To run the unit tests, use the following Maven command:

```bash
mvn test
```

**Service Tests**
    
    - Tests successful retrival of weather data for a given city
    - Also tests the correct error handling when the API call fails
    - Tests the logic and successful output for comparisons of daylight hours between two cities

**Controller Tests**

    - Tests the success of the two API endpoints for the daylight and rain check comparisons between two cities
    - Checks the error handling of the two API endpoints 
