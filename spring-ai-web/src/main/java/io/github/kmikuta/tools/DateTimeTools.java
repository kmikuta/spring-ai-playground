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

  private final WeatherClient weatherClient;

  public DateTimeTools(WeatherClient weatherClient) {
    this.weatherClient = weatherClient;
  }

  @Tool(description = "Get the current date and time for a given city")
  public String getCurrentDateTime(
      @ToolParam(description = "City name e.g. Warsaw, Tokyo, London") String city) {
    LOGGER.info("Using tool: DateTimeTools::getCurrentDateTime for city: {}", city);

    String timezone = weatherClient.getTimezone(city);
    if (timezone == null) {
      return "Timezone not found for city: " + city;
    }

    return LocalDateTime.now(ZoneId.of(timezone)).toString();
  }
}
