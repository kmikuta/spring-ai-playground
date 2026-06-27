package io.github.kmikuta.mcp.clients;

import io.modelcontextprotocol.client.McpSyncClient;
import java.util.List;

public class McpClientFinder {

  private McpClientFinder() {}

  public static McpSyncClient springAiMcpClient(List<McpSyncClient> clients) {
    var mcpSyncClient =
        clients.stream()
            .filter(client -> client.getClientInfo().title().equals("spring_ai_playground"))
            .findFirst()
            .orElseThrow();
    mcpSyncClient.initialize();
    return mcpSyncClient;
  }
}
