package io.github.kmikuta.mcp.domain;

import java.util.Optional;

public interface WeatherClient {
  Optional<Weather> getCurrentTemperature(Location location);
}
