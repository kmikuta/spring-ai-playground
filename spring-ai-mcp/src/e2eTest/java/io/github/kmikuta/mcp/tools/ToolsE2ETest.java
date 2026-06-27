package io.github.kmikuta.mcp.tools;

import static io.github.kmikuta.mcp.clients.McpClientFinder.springAiMcpClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = DEFINED_PORT)
@TestInstance(PER_CLASS)
class ToolsE2ETest {

  @Autowired List<McpSyncClient> mcpSyncClients;

  @Autowired ObjectMapper objectMapper;

  McpSyncClient mcpClient;

  @BeforeAll
  void setUp() {
    mcpClient = springAiMcpClient(mcpSyncClients);
  }

  @AfterAll
  void tearDown() {
    mcpClient.closeGracefully();
  }

  @Test
  void shouldListTools() {
    /* when */
    var tools = mcpClient.listTools();

    /* then */
    assertThat(tools.tools())
        .extracting(McpSchema.Tool::name)
        .contains("getCurrentTemperature", "getCurrentAirQuality");
  }

  @Test
  void shouldReturnTemperature() {
    /* given */
    var request =
        McpSchema.CallToolRequest.builder("getCurrentTemperature")
            .arguments(Map.of("city", "Warsaw"))
            .build();

    /* when */
    var result = mcpClient.callTool(request);

    /* then */
    var content =
        result.content().stream()
            .map(c -> ((McpSchema.TextContent) c).text())
            .map(json -> objectMapper.readValue(json, TemperatureTool.TemperatureResponse.class))
            .findFirst();

    assertThat(content).isPresent();
    assertThat(content.get().temperature()).isInstanceOf(Double.class);
    assertThat(content.get().details()).isEqualTo("Temperature retrieved successfully for Warsaw");
  }

  @Test
  void shouldReturnAirQuality() {
    /* given */
    var request =
        McpSchema.CallToolRequest.builder("getCurrentAirQuality")
            .arguments(Map.of("city", "Lausanne"))
            .build();

    /* when */
    var result = mcpClient.callTool(request);

    /* then */
    var content =
        result.content().stream()
            .map(c -> ((McpSchema.TextContent) c).text())
            .map(json -> objectMapper.readValue(json, AirQualityTool.AirQualityResponse.class))
            .findFirst();

    assertThat(content).isPresent();
    assertThat(content.get().aqi()).isNotNull().isGreaterThanOrEqualTo(0);
    assertThat(content.get().details()).isEqualTo("AQI retrieved successfully for Lausanne");
  }
}
