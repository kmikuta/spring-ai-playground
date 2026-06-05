package io.github.kmikuta.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ChatClientConfig {

  @Bean
  ChatClient simple(ChatModel model) {
    return ChatClient.create(model);
  }

  @Bean
  ChatClient memory(ChatModel model, ChatMemory memory) {
    return ChatClient.builder(model)
        .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
        .build();
  }
}
