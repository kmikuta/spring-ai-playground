package io.github.kmikuta.workflows;

import io.github.kmikuta.tools.AirQualityTools;
import io.github.kmikuta.tools.DateTimeTools;
import io.github.kmikuta.tools.WeatherTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
public class LocationInfoWorkflow {

  private static final String SYSTEM_PROMPT =
      """
      You are a helpful assistant that provides information about locations.
      When given a place name, you MUST:
      1. Call the getCurrentDateTime tool with the city name to get the current local date and time.
      2. Call the getCurrentTemperature tool with the city name to get the current temperature in Celsius.
      3. Call the getCurrentAirQuality tool with the city name to get the current European AQI.
      4. Return a concise summary including the place name, current local time, temperature, and air quality.
      Always call all three tools before responding.
      """;

  private final ChatModel chatModel;
  private final DateTimeTools dateTimeTools;
  private final WeatherTools weatherTools;
  private final AirQualityTools airQualityTools;

  public LocationInfoWorkflow(
      ChatModel chatModel,
      DateTimeTools dateTimeTools,
      WeatherTools weatherTools,
      AirQualityTools airQualityTools) {
    this.chatModel = chatModel;
    this.dateTimeTools = dateTimeTools;
    this.weatherTools = weatherTools;
    this.airQualityTools = airQualityTools;
  }

  public String execute(String place) {
    return ChatClient.builder(chatModel)
        .defaultSystem(SYSTEM_PROMPT)
        .build()
        .prompt()
        .user(place)
        .tools(dateTimeTools, weatherTools, airQualityTools)
        .call()
        .content();
  }
}
