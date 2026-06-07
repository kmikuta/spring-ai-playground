package io.github.kmikuta.api;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import io.github.kmikuta.tools.DateTimeTools;
import io.github.kmikuta.tools.WeatherTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
class ChatController {
  private final ChatModel chatModel;
  private final ChatMemory chatMemory;
  private final DateTimeTools dateTimeTools;
  private final WeatherTools weatherTools;
  private final SyncMcpToolCallbackProvider toolCallbackProvider;

  ChatController(
      ChatModel chatModel,
      ChatMemory chatMemory,
      DateTimeTools dateTimeTools,
      WeatherTools weatherTools,
      SyncMcpToolCallbackProvider toolCallbackProvider) {
    this.chatModel = chatModel;
    this.chatMemory = chatMemory;
    this.dateTimeTools = dateTimeTools;
    this.weatherTools = weatherTools;
    this.toolCallbackProvider = toolCallbackProvider;
  }

  @GetMapping("/simple")
  Response simpleChat(@RequestParam(name = "message") String message) {
    return ChatClient.create(chatModel).prompt(message).call().entity(Response.class);
  }

  @GetMapping("/memory/{conversationId}")
  Response chatWithMemory(
      @PathVariable String conversationId, @RequestParam(name = "message") String message) {
    ChatClient chatClient =
        ChatClient.builder(chatModel)
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            .build();

    return chatClient
        .prompt()
        .user(message)
        .advisors(a -> a.param(CONVERSATION_ID, conversationId))
        .call()
        .entity(Response.class);
  }

  @GetMapping("/tools")
  Response chatWithTools(@RequestParam(name = "message") String message) {
    return ChatClient.create(chatModel)
        .prompt(message)
        .tools(dateTimeTools, weatherTools)
        .call()
        .entity(Response.class);
  }

  @GetMapping("/mcp")
  Response chatWithMcpTools(@RequestParam(name = "content") String content) {
    var prompt = "Generate a report with the following content: " + content;
    return ChatClient.create(chatModel)
        .prompt(prompt)
        .tools(t -> t.callbacks(toolCallbackProvider))
        .call()
        .entity(Response.class);
  }

  record Response(String message) {}
}
