package io.github.kmikuta.workflows.patterns;

import io.github.kmikuta.utils.ModelCallObserver;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import org.springframework.ai.chat.client.ChatClient;

public class ParallelizationWorkflow {

  private final ChatClient chatClient;
  private final ExecutorService executor;
  private final ModelCallObserver callObserver;

  public ParallelizationWorkflow(ChatClient chatClient, ExecutorService executor) {
    this.chatClient = chatClient;
    this.executor = executor;
    this.callObserver = new ModelCallObserver();
  }

  public ParallelizationWorkflow(
      ChatClient chatClient, ExecutorService executor, ModelCallObserver callObserver) {
    this.chatClient = chatClient;
    this.executor = executor;
    this.callObserver = callObserver;
  }

  public List<String> parallel(String prompt, List<String> inputs) {
    List<CompletableFuture<String>> futures =
        inputs.stream().map(input -> execute(prompt, input)).toList();

    CompletableFuture<Void> allFutures =
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    allFutures.join();

    return futures.stream().map(CompletableFuture::join).toList();
  }

  private CompletableFuture<String> execute(String prompt, String input) {
    return CompletableFuture.supplyAsync(
        () -> {
          callObserver.notifyObservers(input);
          return chatClient
              .prompt()
              .user(u -> u.text(prompt).param("input", input))
              .call()
              .content();
        },
        executor);
  }
}
