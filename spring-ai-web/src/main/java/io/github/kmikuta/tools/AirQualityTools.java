package io.github.kmikuta.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class AirQualityTools {
  private static final Logger LOGGER = LoggerFactory.getLogger(AirQualityTools.class);

  private final WeatherClient weatherClient;

  public AirQualityTools(WeatherClient weatherClient) {
    this.weatherClient = weatherClient;
  }

  @Tool(
      description =
          "Get the current European Air Quality Index (AQI) for a given city. Lower is better: 0-20 Good, 20-40 Fair, 40-60 Moderate, 60-80 Poor, 80-100 Very Poor, 100+ Extremely Poor")
  public String getCurrentAirQuality(
      @ToolParam(description = "City name e.g. Warsaw, Tokyo, London") String city) {
    LOGGER.info("Using tool: AirQualityTools::getCurrentAirQuality for city: {}", city);

    Integer aqi = weatherClient.getCurrentAirQuality(city);
    if (aqi == null) {
      return "Air quality data unavailable for " + city;
    }

    return "European AQI in %s is %d".formatted(city, aqi);
  }
}
