package io.github.kmikuta.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class WeatherTools {
  private static final Logger LOGGER = LoggerFactory.getLogger(WeatherTools.class);

  private final WeatherClient weatherClient;

  public WeatherTools(WeatherClient weatherClient) {
    this.weatherClient = weatherClient;
  }

  @Tool(description = "Get the current temperature in Celsius for a given city")
  public String getCurrentTemperature(
      @ToolParam(description = "City name e.g. Warsaw, Tokyo, London") String city) {
    LOGGER.info("Using tool: WeatherTools::getCurrentTemperature for city: {}", city);

    Double temperature = weatherClient.getCurrentTemperature(city);
    if (temperature == null) {
      return "Weather data unavailable for " + city;
    }

    return "%.1f°C in %s".formatted(temperature, city);
  }
}
