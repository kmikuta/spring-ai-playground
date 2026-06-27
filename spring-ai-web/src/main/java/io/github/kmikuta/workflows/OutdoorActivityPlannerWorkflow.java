package io.github.kmikuta.workflows;

import io.github.kmikuta.utils.ModelCallObserver;
import io.github.kmikuta.workflows.patterns.ParallelizationWorkflow;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
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
  private static final Logger LOGGER =
      LoggerFactory.getLogger(OutdoorActivityPlannerWorkflow.class);

  private static final String SYSTEM_PROMPT =
      """
      You are an outdoor activity planner. Collect weather and air quality conditions for {input} location and \
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
      """;

  private final ChatClient chatClient;

  public OutdoorActivityPlannerWorkflow(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  public List<String> execute(List<String> cities) {
    try (ExecutorService executor = Executors.newFixedThreadPool(4)) {
      ModelCallObserver callObserver = new ModelCallObserver();
      callObserver.addObserver(input -> LOGGER.info("Executing prompt for input: {}", input));

      ParallelizationWorkflow workflow =
          new ParallelizationWorkflow(chatClient, executor, callObserver);
      return workflow.parallel(SYSTEM_PROMPT, cities);
    }
  }
}
