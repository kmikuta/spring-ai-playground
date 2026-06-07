package io.github.kmikuta.workflows;

import io.github.kmikuta.tools.AirQualityTools;
import io.github.kmikuta.tools.WeatherTools;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

/**
 * Implements the Parallelization Workflow agentic pattern.
 *
 * @see <a href="https://spring.io/blog/2025/01/21/spring-ai-agentic-patterns">Spring AI Agentic
 *     Patterns</a>
 * @see <a href="https://www.anthropic.com/research/building-effective-agents">Building Effective
 *     Agents</a>
 */
@Component
public class OutdoorActivityPlannerWorkflow {

  private static final String TEMPERATURE_PROMPT =
      "Get the current temperature in Celsius for the following city:";

  private static final String AIR_QUALITY_PROMPT =
      "Get the current European Air Quality Index (AQI) for the following city:";

  private static final String AGGREGATION_PROMPT =
      """
      You are an outdoor activity planner. Based on the weather conditions provided, \
      recommend 3 outdoor activities suitable for those conditions.

      Guidelines:
      - Temperature below 0°C: winter activities (ice skating, skiing, snowshoeing)
      - Temperature 0–10°C: brisk-weather activities (hiking, cycling, outdoor markets)
      - Temperature 10–25°C: most activities (running, picnics, sightseeing, sports)
      - Temperature above 25°C: shaded or water-based activities (swimming, early-morning walks)
      - AQI 0–40 (Good/Fair): all outdoor activities are fine
      - AQI 40–60 (Moderate): avoid sustained vigorous exercise; light activities are okay
      - AQI above 60 (Poor or worse): only brief low-intensity outings; suggest indoor alternatives

      Present 3 recommendations with a short rationale that references the actual values.

      Weather conditions:
      """;

  private final ChatModel chatModel;
  private final WeatherTools weatherTools;
  private final AirQualityTools airQualityTools;

  public OutdoorActivityPlannerWorkflow(
      ChatModel chatModel, WeatherTools weatherTools, AirQualityTools airQualityTools) {
    this.chatModel = chatModel;
    this.weatherTools = weatherTools;
    this.airQualityTools = airQualityTools;
  }

  public String execute(String city) {
    ExecutorService executor = Executors.newFixedThreadPool(2);
    try {
      CompletableFuture<String> temperatureFuture =
          CompletableFuture.supplyAsync(
              () ->
                  ChatClient.builder(chatModel)
                      .build()
                      .prompt(TEMPERATURE_PROMPT + "\nInput: " + city)
                      .tools(weatherTools)
                      .call()
                      .content(),
              executor);

      CompletableFuture<String> airQualityFuture =
          CompletableFuture.supplyAsync(
              () ->
                  ChatClient.builder(chatModel)
                      .build()
                      .prompt(AIR_QUALITY_PROMPT + "\nInput: " + city)
                      .tools(airQualityTools)
                      .call()
                      .content(),
              executor);

      CompletableFuture.allOf(temperatureFuture, airQualityFuture).join();

      String conditions =
          "Temperature: " + temperatureFuture.join() + "\nAir Quality: " + airQualityFuture.join();

      return ChatClient.builder(chatModel)
          .build()
          .prompt(AGGREGATION_PROMPT + conditions)
          .call()
          .content();
    } finally {
      executor.shutdown();
    }
  }
}
