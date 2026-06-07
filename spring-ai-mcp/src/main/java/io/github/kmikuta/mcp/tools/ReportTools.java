package io.github.kmikuta.mcp.tools;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
class ReportTools {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReportTools.class);

  @McpTool(name = "generateReport", description = "Generates a report")
  public Report generate(@McpToolParam(description = "Content of the report") String content) {
    LOGGER.info("Using tool: ReportTools::generate for content: {}", content);
    var date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    return new Report(content, date, Status.SUCCESS);
  }

  record Report(String content, String date, Status status) {}

  enum Status {
    SUCCESS,
    FAILURE
  }
}
