package io.github.kmikuta.config;

import io.github.kmikuta.tools.DateTimeTools;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@NullMarked
class ChatClientConfig {

  @Bean
  ChatClient chatClient(
      ChatModel chatModel,
      @Autowired(required = false) @Nullable SyncMcpToolCallbackProvider toolCallbackProvider,
      DateTimeTools timeTools) {
    var builder = ChatClient.builder(chatModel);
    builder.defaultTools(timeTools);

    if (toolCallbackProvider != null) {
      builder.defaultTools(t -> t.callbacks(toolCallbackProvider));
    }

    return builder.build();
  }
}
