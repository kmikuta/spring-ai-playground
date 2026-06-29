package io.github.kmikuta.tools;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class DateTimeTools {
  private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeTools.class);

  @Tool(description = "Get the current date and time for given timezone")
  public String getDateTimeForTimezone(
      @ToolParam(description = "Timezone identifier e.g. Europe/Warsaw") String timeZone) {
    LOGGER.info("Using tool: DateTimeTools::getCurrentDateTime for city: {}", timeZone);
    return LocalDateTime.now(ZoneId.of(timeZone)).toString();
  }
}
