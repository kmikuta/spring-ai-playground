package io.github.kmikuta.adapters;

import io.github.kmikuta.tools.WeatherClient;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OpenMeteoWeatherClient implements WeatherClient {

  private final RestClient restClient = RestClient.create();
  private final Map<String, GeocodingResult> geocodingCache = new ConcurrentHashMap<>();

  @Override
  public Double getCurrentTemperature(String city) {
    GeocodingResult location = geocode(city);
    if (location == null) {
      return null;
    }

    WeatherResponse weather =
        restClient
            .get()
            .uri(
                "https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current=temperature_2m",
                location.latitude(),
                location.longitude())
            .retrieve()
            .body(WeatherResponse.class);

    return weather != null && weather.current() != null ? weather.current().temperature_2m() : null;
  }

  @Override
  public String getTimezone(String city) {
    GeocodingResult location = geocode(city);
    return location != null ? location.timezone() : null;
  }

  private GeocodingResult geocode(String city) {
    return geocodingCache.computeIfAbsent(
        city.toLowerCase(),
        key -> {
          GeocodingResponse response =
              restClient
                  .get()
                  .uri(
                      "https://geocoding-api.open-meteo.com/v1/search?name={city}&count=1&language=en&format=json",
                      city)
                  .retrieve()
                  .body(GeocodingResponse.class);

          if (response == null || response.results() == null || response.results().isEmpty()) {
            return null;
          }

          return response.results().getFirst();
        });
  }

  private record GeocodingResponse(List<GeocodingResult> results) {}

  private record GeocodingResult(
      String name, String country, double latitude, double longitude, String timezone) {}

  private record WeatherResponse(CurrentWeather current) {}

  private record CurrentWeather(double temperature_2m) {}
}
