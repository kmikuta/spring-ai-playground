package io.github.kmikuta.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Configuration
class ChatClientConfig {

  @Bean
  ChatClient chatClient(
      ChatModel chatModel,
      @Autowired(required = false) @Nullable SyncMcpToolCallbackProvider toolCallbackProvider) {
    var builder = ChatClient.builder(chatModel);
    if (toolCallbackProvider != null) {
      builder.defaultTools(t -> t.callbacks(toolCallbackProvider));
    }
    return builder.build();
  }
}
