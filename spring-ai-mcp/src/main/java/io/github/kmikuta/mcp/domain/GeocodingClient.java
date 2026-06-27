package io.github.kmikuta.mcp.domain;

import java.util.Optional;

public interface GeocodingClient {
  Optional<Geocoding> getGeocoding(String city);
}
