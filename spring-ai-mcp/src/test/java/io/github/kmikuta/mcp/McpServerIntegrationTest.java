package io.github.kmikuta.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class McpServerIntegrationTest {

  @LocalServerPort
  private int port;

  private McpSyncClient mcpClient;

  @BeforeEach
  void setUp() {
    var transport =
        HttpClientStreamableHttpTransport.builder("http://localhost:" + port).build();
    mcpClient =
        McpClient.sync(transport)
            .clientInfo(McpSchema.Implementation.builder("test-client", "1.0").build())
            .build();
    mcpClient.initialize();
  }

  @AfterEach
  void tearDown() {
    mcpClient.closeGracefully();
  }

  @Test
  void listsGenerateReportTool() {
    // when
    var tools = mcpClient.listTools();

    // then
    assertThat(tools.tools())
        .extracting(McpSchema.Tool::name)
        .contains("generateReport");
  }

  @Test
  void callsGenerateReportTool() {
    // given
    var request =
        McpSchema.CallToolRequest.builder("generateReport")
            .arguments(Map.of("date", "2026-06-05"))
            .build();

    // when
    var result = mcpClient.callTool(request);

    // then
    assertThat(result.isError()).isFalse();
    assertThat(result.content()).isNotEmpty();
  }
}
