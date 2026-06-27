package io.github.kmikuta.mcp.adapters;

import io.github.kmikuta.mcp.domain.Location;
import io.github.kmikuta.mcp.domain.Weather;
import io.github.kmikuta.mcp.domain.WeatherClient;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Profile("!fakeApi")
@NullMarked
public class OpenMeteoWeatherClient implements WeatherClient {
  private static final String API_URL =
      "https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current=temperature_2m";

  private final RestClient restClient;

  public OpenMeteoWeatherClient(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public Optional<Weather> getCurrentTemperature(Location location) {
    WeatherResponse weather =
        restClient
            .get()
            .uri(API_URL, location.latitude(), location.longitude())
            .retrieve()
            .body(WeatherResponse.class);

    return Optional.ofNullable(weather).map(WeatherResponse::current);
  }

  private record WeatherResponse(@Nullable Weather current) {}
}
