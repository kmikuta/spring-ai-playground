package io.github.kmikuta.workflows.patterns;

import io.github.kmikuta.utils.ModelCallObserver;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;

public class OrchestratorWorkflow {
  private static final String INTERNAL_ORCHESTRATION_PROMPT =
      """
            # Instruction
            {orchestration}

            # Format
            Return them in the following format:

            [
              \\{  name: “Task 1 name”, prompt: “Task 1 description” \\},
              \\{  name: “Task 2 name”, prompt: “Task 2 description” \\}
            ]

            # Task
            {task}
    """;

  private final ChatClient chatClient;

  private final String externalOrchestrationPrompt;

  private final ModelCallObserver modelCallObserver;

  public OrchestratorWorkflow(
      ChatClient chatClient,
      String externalOrchestrationPrompt,
      ModelCallObserver modelCallObserver) {
    this.chatClient = chatClient;
    this.externalOrchestrationPrompt = externalOrchestrationPrompt;
    this.modelCallObserver = modelCallObserver;
  }

  public OrchestratorResponse execute(String task) {
    OrchestratorResponse response =
        chatClient
            .prompt()
            .user(
                u ->
                    u.text(INTERNAL_ORCHESTRATION_PROMPT)
                        .param("orchestration", externalOrchestrationPrompt)
                        .param("task", task))
            .call()
            .entity(OrchestratorResponse.class);

    response
        .tasks()
        .forEach(
            t -> {
              modelCallObserver.notifyObservers(t.toString());
            });

    return response;
  }

  public record Task(String name, String prompt) {}

  public record OrchestratorResponse(List<Task> tasks) {}
}
