package io.github.kmikuta.mcp.domain;

public record Location(double latitude, double longitude) {
  public static Location from(Geocoding geocodingResult) {
    return new Location(geocodingResult.latitude(), geocodingResult.longitude());
  }
}
