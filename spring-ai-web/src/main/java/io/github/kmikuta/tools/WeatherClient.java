package io.github.kmikuta.tools;

public interface WeatherClient {

  Double getCurrentTemperature(String city);

  String getTimezone(String city);
}
