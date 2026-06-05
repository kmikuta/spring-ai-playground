package io.github.kmikuta.tools;

import java.time.ZoneId;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class WeatherTools {
  private static final Logger LOGGER = LoggerFactory.getLogger(WeatherTools.class);

  private static final Map<ZoneId, Number> temperature =
      Map.of(
          ZoneId.of("Europe/Warsaw"), 18,
          ZoneId.of("America/Los_Angeles"), 24);

  @Tool(description = "Get the current temperature in Celsius in the given timezone")
  public Number getCurrentTemperature(
      @ToolParam(description = "Timezone id e.g. Europe/Warsaw") String zoneId) {
    LOGGER.info("Using tool: DateTimeTools::getCurrentWeather for timezone: {}", zoneId);
    return temperature.get(ZoneId.of(zoneId));
  }
}
