package io.github.kmikuta.workflows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
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
public class LocationOrActivityWorkflow {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocationOrActivityWorkflow.class);

  private static final String CLASSIFIER_PROMPT =
      """
            Classify the following user prompt into exactly one route and extract the relevant parameter.

            Routes:
            - LOCATION_INFO: questions about current date/time, weather, or air quality for a specific place
            - OUTDOOR_ACTIVITIES: requests for outdoor activity recommendations for a specific city
            - GENERAL: anything else

            Respond with a JSON object:
              {"route": "LOCATION_INFO", "parameter": "Tokyo"}
              {"route": "OUTDOOR_ACTIVITIES", "parameter": "Warsaw"}
              {"route": "GENERAL", "parameter": ""}

            User prompt: {prompt}
            """;

  private final ChatClient chatClient;
  private final LocationInfoWorkflow locationInfoWorkflow;
  private final OutdoorActivityPlannerWorkflow outdoorActivityPlannerWorkflow;

  public LocationOrActivityWorkflow(
      ChatModel chatModel,
      LocationInfoWorkflow locationInfoWorkflow,
      OutdoorActivityPlannerWorkflow outdoorActivityPlannerWorkflow) {
    this.chatClient = ChatClient.builder(chatModel).build();
    this.locationInfoWorkflow = locationInfoWorkflow;
    this.outdoorActivityPlannerWorkflow = outdoorActivityPlannerWorkflow;
  }

  public String execute(String prompt) {
    RouteDecision decision = classify(prompt);
    LOGGER.info(
        "Routing '{}' → {} (parameter: {})", prompt, decision.route(), decision.parameter());

    return switch (decision.route()) {
      case LOCATION_INFO -> locationInfoWorkflow.execute(decision.parameter());
      case OUTDOOR_ACTIVITIES -> outdoorActivityPlannerWorkflow.execute(decision.parameter());
      case GENERAL -> chatClient.prompt(prompt).call().content();
    };
  }

  private RouteDecision classify(String prompt) {
    return chatClient
        .prompt(CLASSIFIER_PROMPT.replace("{prompt}", prompt))
        .call()
        .entity(RouteDecision.class);
  }

  enum Route {
    LOCATION_INFO,
    OUTDOOR_ACTIVITIES,
    GENERAL
  }

  record RouteDecision(Route route, String parameter) {}
}
