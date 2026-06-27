package io.github.kmikuta.mcp.tools;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.kmikuta.mcp.adapters.FakeGeocodingClient;
import io.github.kmikuta.mcp.adapters.FakeWeatherClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(classes = {FakeWeatherClient.class, FakeGeocodingClient.class, ObjectMapper.class})
class TemperatureToolTest {

  @Autowired FakeWeatherClient fakeWeatherClient;

  @Autowired FakeGeocodingClient fakeGeocodingClient;

  TemperatureTool temperatureTool;

  @BeforeEach
  void setUp() {
    temperatureTool = new TemperatureTool(fakeWeatherClient, fakeGeocodingClient);
  }

  @AfterEach
  void tearDown() {
    fakeWeatherClient.reset();
    fakeGeocodingClient.reset();
  }

  @Test
  void shouldReturnErrorResponseForFailingGeocodingClient() {
    /* given */
    fakeGeocodingClient.willFailForAllRequests();

    /* when */
    var warsaw = temperatureTool.getCurrentTemperature("Warsaw");

    /* then */
    assertThat(warsaw.temperature()).isNull();
    assertThat(warsaw.details()).isEqualTo("Weather data unavailable for Warsaw");
  }

  @Test
  void shouldReturnErrorResponseForFailingWeatherClient() {
    /* given */
    fakeWeatherClient.willFailForAllRequests();

    /* when */
    var warsaw = temperatureTool.getCurrentTemperature("Warsaw");

    /* then */
    assertThat(warsaw.temperature()).isNull();
    assertThat(warsaw.details()).isEqualTo("Weather data unavailable for Warsaw");
  }

  @Test
  void shouldReturnErrorResponseForUnknownCity() {
    /* when */
    var warsaw = temperatureTool.getCurrentTemperature("Unknown");

    /* then */
    assertThat(warsaw.temperature()).isNull();
    assertThat(warsaw.details()).isEqualTo("Weather data unavailable for Unknown");
  }

  @Test
  void shouldReturnTemperatureForGivenCity() {
    /* when */
    var warsaw = temperatureTool.getCurrentTemperature("Warsaw");

    /* then */
    assertThat(warsaw.temperature()).isEqualTo(33.5d);
    assertThat(warsaw.details()).isEqualTo("Temperature retrieved successfully for Warsaw");
  }
}
