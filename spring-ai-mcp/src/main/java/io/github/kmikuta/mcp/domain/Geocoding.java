package io.github.kmikuta.mcp.domain;

public record Geocoding(
    String name, String country, double latitude, double longitude, String timezone) {}
