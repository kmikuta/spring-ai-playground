package io.github.kmikuta.workflows;

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
      3. Return a concise summary including the place name, current local time, and temperature.
      Always call both tools before responding.
      """;

  private final ChatModel chatModel;
  private final DateTimeTools dateTimeTools;
  private final WeatherTools weatherTools;

  public LocationInfoWorkflow(
      ChatModel chatModel, DateTimeTools dateTimeTools, WeatherTools weatherTools) {
    this.chatModel = chatModel;
    this.dateTimeTools = dateTimeTools;
    this.weatherTools = weatherTools;
  }

  public String execute(String place) {
    return ChatClient.builder(chatModel)
        .defaultSystem(SYSTEM_PROMPT)
        .build()
        .prompt()
        .user(place)
        .tools(dateTimeTools, weatherTools)
        .call()
        .content();
  }
}
