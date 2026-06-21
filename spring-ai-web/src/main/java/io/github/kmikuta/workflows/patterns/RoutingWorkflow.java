package io.github.kmikuta.workflows.patterns;

import io.github.kmikuta.utils.ModelCallObserver;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;

public class RoutingWorkflow {

  private static final String ROUTING_PROMPT =
      """
        Analyze the given prompt and classify it as one of the given routes: {supportedRoutes}
        Respond in the following json format:

        # Format
        \\{
            "choice": "classified route",
            "prompt": "{prompt}"
        \\}

        # Prompt
        {prompt}
    """;

  private final ChatClient chatClient;
  private final ModelCallObserver callObserver;

  public RoutingWorkflow(ChatClient chatClient) {
    this.chatClient = chatClient;
    this.callObserver = new ModelCallObserver();
  }

  public RoutingWorkflow(ChatClient chatClient, ModelCallObserver callObserver) {
    this.chatClient = chatClient;
    this.callObserver = callObserver;
  }

  public String route(String prompt, Map<String, String> supportedRoutes) {
    var response =
        chatClient
            .prompt()
            .user(
                u ->
                    u.text(ROUTING_PROMPT)
                        .param("supportedRoutes", supportedRoutes)
                        .param("prompt", prompt))
            .call()
            .entity(RoutingChoice.class);

    callObserver.notifyObservers(response.toString());

    var targetPrompt = supportedRoutes.get(response.choice());
    if (targetPrompt == null) {
      return "Unsupported routing detected for the prompt: " + prompt;
    }

    return chatClient
        .prompt()
        .user(u -> u.text(targetPrompt).param("input", prompt))
        .call()
        .content();
  }

  public record RoutingChoice(String prompt, String choice) {}
}
