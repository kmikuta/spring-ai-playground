package io.github.kmikuta.api;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/chat")
class ChatController {

  private final ChatClient simpleChat;

  private final ChatClient memoryChat;

  ChatController(@Qualifier("simple") ChatClient simpleChat, @Qualifier("memory") ChatClient memoryChat) {
    this.simpleChat = simpleChat;
    this.memoryChat = memoryChat;
  }

  @GetMapping(value = "/simple", produces = MediaType.APPLICATION_JSON_VALUE)
  Hello simpleChat(@RequestParam(name = "message") String message) {
    return simpleChat.prompt(message).call().entity(Hello.class);
  }

  @GetMapping(value = "/memory/{conversationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  Hello chatWithMemory(@PathVariable String conversationId, @RequestParam(name = "message") String message) {
    return memoryChat.prompt(message)
            .advisors(a -> a.param(CONVERSATION_ID, conversationId))
            .call()
            .entity(Hello.class);
  }

  record Hello(String message) {}
}
