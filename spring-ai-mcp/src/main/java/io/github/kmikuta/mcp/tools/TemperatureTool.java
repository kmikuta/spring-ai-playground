package io.github.kmikuta.mcp.tools;

import io.github.kmikuta.mcp.domain.GeocodingClient;
import io.github.kmikuta.mcp.domain.Location;
import io.github.kmikuta.mcp.domain.WeatherClient;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@NullMarked
class TemperatureTool {
  private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureTool.class);

  private static final String TOOL_DESCRIPTION =
      "Get the current temperature in Celsius for a given city";
  private static final String TOOL_PARAM_DESCRIPTION = "City name e.g. Warsaw, Tokyo, London";

  private final WeatherClient weatherClient;
  private final GeocodingClient geocodingClient;

  TemperatureTool(WeatherClient weatherClient, GeocodingClient geocodingClient) {
    this.weatherClient = weatherClient;
    this.geocodingClient = geocodingClient;
  }

  @McpTool(description = TOOL_DESCRIPTION)
  TemperatureResponse getCurrentTemperature(
      @McpToolParam(description = TOOL_PARAM_DESCRIPTION) String city) {
    LOGGER.info("Using WeatherTool for city: {}", city);

    try {
      return geocodingClient
          .getGeocoding(city)
          .map(Location::from)
          .flatMap(weatherClient::getCurrentTemperature)
          .map(weather -> TemperatureResponse.success(city, weather.temperature_2m()))
          .orElseGet(() -> TemperatureResponse.error(city));
    } catch (RestClientException e) {
      LOGGER.error("Temperature tool threw exception: {}", e.getMessage());
      return TemperatureResponse.error(city);
    }
  }

  record TemperatureResponse(@Nullable Double temperature, String details) {
    static TemperatureResponse success(String city, Double temperature) {
      return new TemperatureResponse(temperature, "Temperature retrieved successfully for " + city);
    }

    static TemperatureResponse error(String city) {
      return new TemperatureResponse(null, "Weather data unavailable for " + city);
    }
  }
}
