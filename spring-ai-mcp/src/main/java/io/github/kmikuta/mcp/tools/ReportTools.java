package io.github.kmikuta.mcp.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class ReportTools {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReportTools.class);

  @McpTool(name = "generateReport", description = "Generates a report")
  public Report generate(@McpToolParam(description = "Date of the report") String date) {
    LOGGER.info("Using tool: ReportTools::generate for date: {}", date);
    return new Report(date, UUID.randomUUID(), Status.SUCCESS);
  }

  record Report(String date, UUID id, Status status) {}

  enum Status {
    SUCCESS,
    FAILURE
  }
}
