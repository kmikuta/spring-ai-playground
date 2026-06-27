package io.github.kmikuta.workflows;

import io.github.kmikuta.utils.ModelCallObserver;
import io.github.kmikuta.workflows.patterns.RoutingWorkflow;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Implements the Routing Workflow agentic pattern.
 *
 * @see <a href="https://spring.io/blog/2025/01/21/spring-ai-agentic-patterns">Spring AI Agentic
 *     Patterns</a>
 * @see <a href="https://www.anthropic.com/research/building-effective-agents">Building Effective
 *     Agents</a>
 */
@Component
public class WeatherOrAirQualityWorkflow {
  private static final Logger LOGGER = LoggerFactory.getLogger(WeatherOrAirQualityWorkflow.class);

  private static final Map<String, String> ROUTES =
      Map.of(
          "weather",
              """
                    You are a weather expert.

                    Analyze the input, extract location and call the getCurrentTemperature tool
                    to get the current temperature in Celsius.

                    # Input
                    {input}
                """,
          "airQuality",
              """
                    You are an air quality expert.

                    Analyze the input, extract location and call the getCurrentAirQuality tool
                    to get the current European AQI.

                    # Input
                    {input}
                """);

  private final ChatClient chatClient;

  public WeatherOrAirQualityWorkflow(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  public String execute(String prompt) {
    ModelCallObserver modelCallObserver = new ModelCallObserver();
    modelCallObserver.addObserver(response -> LOGGER.info("Routing choice: {}", response));

    RoutingWorkflow workflow = new RoutingWorkflow(chatClient, modelCallObserver);
    return workflow.route(prompt, ROUTES);
  }
}
