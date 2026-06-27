package io.github.kmikuta.mcp.adapters;

import io.github.kmikuta.mcp.domain.Geocoding;
import io.github.kmikuta.mcp.domain.GeocodingClient;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Profile("!fakeApi")
@NullMarked
public class OpenMeteoGeocodingClient implements GeocodingClient {
  private static final String API_URL =
      "https://geocoding-api.open-meteo.com/v1/search?name={city}&count=1&language=en&format=json";

  private final RestClient restClient;

  public OpenMeteoGeocodingClient(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public Optional<Geocoding> getGeocoding(String city) {
    GeocodingResponse response =
        restClient.get().uri(API_URL, city).retrieve().body(GeocodingResponse.class);

    if (response == null || response.results() == null || response.results().isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(response.results().getFirst());
  }

  private record GeocodingResponse(@Nullable List<Geocoding> results) {}
}
