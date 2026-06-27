package io.github.kmikuta.api;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
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

  ChatController(ChatModel chatModel, ChatMemory chatMemory) {
    this.chatModel = chatModel;
    this.chatMemory = chatMemory;
  }

  /** Example: GET /api/chat/simple?message=Hello */
  @GetMapping("/simple")
  @Operation(summary = "Simple chat call")
  Response simpleChat(@RequestParam(name = "message") String message) {
    return ChatClient.create(chatModel).prompt(message).call().entity(Response.class);
  }

  /**
   * Example: GET /api/chat/memory/session-123?message=What is the capital of France? Follow-up: GET
   * /api/chat/memory/session-123?message=What did I just ask about?
   */
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

  record Response(String message) {}
}
