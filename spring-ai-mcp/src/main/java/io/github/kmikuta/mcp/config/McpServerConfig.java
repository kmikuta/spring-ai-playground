package io.github.kmikuta.mcp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class McpServerConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(McpServerConfig.class);

  @Bean
  ToolExecutionExceptionProcessor toolExecutionExceptionProcessor() {
    return (ToolExecutionException exception) -> {
      String toolName = exception.getToolDefinition().name();
      String message = exception.getMessage();

      LOGGER.error("MCP Tool {} failed: {}", toolName, message);

      return String.format(
          "An error occurred while executing the MCP tool %s: %s", toolName, message);
    };
  }
}
