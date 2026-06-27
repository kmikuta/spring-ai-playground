package io.github.kmikuta.mcp.domain;

import java.util.Optional;

public interface AirQualityClient {
  Optional<AirQuality> getCurrentAirQuality(Location location);
}
