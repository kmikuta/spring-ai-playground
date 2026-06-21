package io.github.kmikuta.workflows.patterns;

import io.github.kmikuta.utils.ModelCallObserver;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;

public class ChainWorkflow {

  private final ChatClient chatClient;
  private final List<String> systemPrompts;
  private final ModelCallObserver callObserver;

  public ChainWorkflow(ChatClient chatClient, List<String> systemPrompts) {
    this.chatClient = chatClient;
    this.systemPrompts = systemPrompts;
    this.callObserver = new ModelCallObserver();
  }

  public ChainWorkflow(
      ChatClient chatClient, List<String> systemPrompts, ModelCallObserver callObserver) {
    this.chatClient = chatClient;
    this.systemPrompts = systemPrompts;
    this.callObserver = callObserver;
  }

  public String chain(String userInput) {
    String response = String.format(userInput);

    for (String prompt : systemPrompts) {
      callObserver.notifyObservers(response);
      String input = String.format("{%s}\n\n{%s}", response, prompt);
      response = chatClient.prompt(input).call().content();
    }

    return response;
  }
}
