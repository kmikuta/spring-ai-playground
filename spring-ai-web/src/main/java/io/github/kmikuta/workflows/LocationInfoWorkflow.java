package io.github.kmikuta.workflows;

import io.github.kmikuta.utils.ModelCallObserver;
import io.github.kmikuta.workflows.patterns.ChainWorkflow;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Implements the Chain Workflow agentic pattern.
 *
 * @see <a href="https://spring.io/blog/2025/01/21/spring-ai-agentic-patterns">Spring AI Agentic
 *     Patterns</a>
 * @see <a href="https://www.anthropic.com/research/building-effective-agents">Building Effective
 *     Agents</a>
 */
@Component
public class LocationInfoWorkflow {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocationInfoWorkflow.class);

  private static final String[] SYSTEM_PROMPTS = {
    /* Step 1: retrieve current temperature for the location */
    """
    For given location, call the getCurrentTemperature tool to get the current temperature in Celsius.
    Respond in the following format: 'Location: Warsaw | Temperature: 25°C'
    """,
    /* Step 2: retrieve current air quality */
    """
    For given location, call the getCurrentAirQuality tool to get the current European AQI.
    Respond in the following format: 'Location: Warsaw | Temperature: 25°C | Air Quality (AQI): 40'
    """,
    /* Step 3: format the final summary */
    """
    For given location, temperature, and air quality, format a concise readable summary.
    """
  };

  private final ChatClient chatClient;

  public LocationInfoWorkflow(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  public String execute(String place) {
    ModelCallObserver callObserver = new ModelCallObserver();
    callObserver.addObserver((response) -> LOGGER.info("Accumulated response: {}", response));

    ChainWorkflow workflow =
        new ChainWorkflow(chatClient, Arrays.asList(SYSTEM_PROMPTS), callObserver);
    return workflow.chain("Location: " + place);
  }
}
