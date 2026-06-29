package io.github.kmikuta.workflows;

import io.github.kmikuta.utils.ModelCallObserver;
import io.github.kmikuta.workflows.patterns.OrchestratorWorkflow;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Implements the Orchestrator Workflow agentic pattern.
 *
 * @see <a href="https://spring.io/blog/2025/01/21/spring-ai-agentic-patterns">Spring AI Agentic
 *     Patterns</a>
 * @see <a href="https://www.anthropic.com/research/building-effective-agents">Building Effective
 *     Agents</a>
 */
@Component
public class GeneralPlannerWorkflow {
  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPlannerWorkflow.class);

  private static final String ORCHESTRATION_PROMPT =
      """
            You are professional trip advisor.

            When given a city name and optionally user’s preference, you are able to plan the outdoor activities
            taking into consideration the weather, current time, coordinates and air quality.

            When user does not provide preferences, just assume they are open for every type of outdoor activity.

            Use available tools: getGeocoding, getCurrentWeather, getCurrentAirQuality, getDateTimeForTimezone.

            Analyze user’s input and split the planning into smaller tasks (max 5) that can be executed in parallel.
        """;

  private static final String SUMMARIZATION_PROMPT =
      """
           Summarize the results.

           # Results
           {results}
       """;

  private final ChatClient chatClient;

  public GeneralPlannerWorkflow(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  public String execute(String prompt) {
    ModelCallObserver modelCallObserver = new ModelCallObserver();
    modelCallObserver.addObserver(response -> LOGGER.info("Orchestrator task: {}", response));

    OrchestratorWorkflow workflow =
        new OrchestratorWorkflow(chatClient, ORCHESTRATION_PROMPT, modelCallObserver);

    List<String> results;
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      List<Future<String>> futures =
          workflow.execute(prompt).tasks().stream()
              .map(task -> new ChatClientCall(chatClient, task))
              .map(executor::submit)
              .toList();

      results = futures.stream().map(ChatClientCall::readValue).toList();
    }

    return summarize(results);
  }

  private String summarize(List<String> results) {
    return chatClient
        .prompt()
        .user(u -> u.text(SUMMARIZATION_PROMPT).param("results", results))
        .call()
        .content();
  }

  private record ChatClientCall(ChatClient chatClient, OrchestratorWorkflow.Task task)
      implements Callable<String> {
    static String readValue(Future<String> future) {
      try {
        return future.get();
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }

    @Override
    public String call() {
      LOGGER.info("Executing: {}", task.prompt());
      return chatClient.prompt(task.prompt()).call().content();
    }
  }
}
