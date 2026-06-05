package io.github.kmikuta.tools;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class DateTimeTools {
  private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeTools.class);

  @Tool(description = "Get the current date and time in the given timezone")
  public String getCurrentDateTime(
      @ToolParam(description = "Timezone id e.g. Europe/Warsaw") String zoneId) {
    LOGGER.info("Using tool: DateTimeTools::getCurrentDateTime for timezone: {}", zoneId);
    return LocalDateTime.now(ZoneId.of(zoneId)).toString();
  }
}
