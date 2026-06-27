package io.github.kmikuta.mcp.tools;

import io.github.kmikuta.mcp.domain.AirQualityClient;
import io.github.kmikuta.mcp.domain.GeocodingClient;
import io.github.kmikuta.mcp.domain.Location;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@NullMarked
@Component
class AirQualityTool {
  private static final Logger LOGGER = LoggerFactory.getLogger(AirQualityTool.class);

  private static final String TOOL_DESCRIPTION =
      "Gets the current European Air Quality Index (AQI) for a given city.";
  private static final String TOOL_PARAM_DESCRIPTION = "City name e.g. Warsaw, Tokyo, London";

  private final GeocodingClient geocodingClient;
  private final AirQualityClient airQualityClient;

  AirQualityTool(GeocodingClient geocodingClient, AirQualityClient airQualityClient) {
    this.geocodingClient = geocodingClient;
    this.airQualityClient = airQualityClient;
  }

  @McpTool(description = TOOL_DESCRIPTION)
  AirQualityResponse getCurrentAirQuality(
      @McpToolParam(description = TOOL_PARAM_DESCRIPTION) String city) {
    LOGGER.info("Using AirQualityTool for city: {}", city);

    try {
      return geocodingClient
          .getGeocoding(city)
          .map(Location::from)
          .flatMap(airQualityClient::getCurrentAirQuality)
          .map(airQuality -> AirQualityResponse.success(city, airQuality.european_aqi()))
          .orElseGet(() -> AirQualityResponse.error(city));
    } catch (RestClientException e) {
      LOGGER.error("Air quality tool threw exception: {}", e.getMessage());
      return AirQualityResponse.error(city);
    }
  }

  record AirQualityResponse(@Nullable Integer aqi, String details) {
    static AirQualityResponse success(String city, Integer aqi) {
      return new AirQualityResponse(aqi, "AQI retrieved successfully for " + city);
    }

    static AirQualityResponse error(String city) {
      return new AirQualityResponse(null, "Air quality data unavailable for " + city);
    }
  }
}
