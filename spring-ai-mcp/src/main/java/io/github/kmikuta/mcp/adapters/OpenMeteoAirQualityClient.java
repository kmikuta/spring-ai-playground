package io.github.kmikuta.mcp.adapters;

import io.github.kmikuta.mcp.domain.AirQuality;
import io.github.kmikuta.mcp.domain.AirQualityClient;
import io.github.kmikuta.mcp.domain.Location;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Profile("!fakeApi")
@NullMarked
public class OpenMeteoAirQualityClient implements AirQualityClient {
  private static final String API_URL =
      "https://air-quality-api.open-meteo.com/v1/air-quality?latitude={lat}&longitude={lon}&current=european_aqi";

  private final RestClient restClient;

  public OpenMeteoAirQualityClient(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public Optional<AirQuality> getCurrentAirQuality(Location location) {
    AirQualityResponse airQuality =
        restClient
            .get()
            .uri(API_URL, location.latitude(), location.longitude())
            .retrieve()
            .body(AirQualityResponse.class);

    return Optional.ofNullable(airQuality).map(AirQualityResponse::current);
  }

  private record AirQualityResponse(@Nullable AirQuality current) {}
}
