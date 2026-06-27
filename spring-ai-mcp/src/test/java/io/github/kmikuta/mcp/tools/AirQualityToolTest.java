package io.github.kmikuta.mcp.tools;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.kmikuta.mcp.adapters.FakeAirQualityClient;
import io.github.kmikuta.mcp.adapters.FakeGeocodingClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(
    classes = {FakeAirQualityClient.class, FakeGeocodingClient.class, ObjectMapper.class})
class AirQualityToolTest {

  @Autowired FakeAirQualityClient fakeAirQualityClient;

  @Autowired FakeGeocodingClient fakeGeocodingClient;

  AirQualityTool airQualityTool;

  @BeforeEach
  void setUp() {
    airQualityTool = new AirQualityTool(fakeGeocodingClient, fakeAirQualityClient);
  }

  @AfterEach
  void tearDown() {
    fakeAirQualityClient.reset();
    fakeGeocodingClient.reset();
  }

  @Test
  void shouldReturnErrorResponseForFailingGeocodingClient() {
    /* given */
    fakeGeocodingClient.willFailForAllRequests();

    /* when */
    var warsaw = airQualityTool.getCurrentAirQuality("Warsaw");

    /* then */
    assertThat(warsaw.aqi()).isNull();
    assertThat(warsaw.details()).isEqualTo("Air quality data unavailable for Warsaw");
  }

  @Test
  void shouldReturnErrorResponseForFailingAirQualityClient() {
    /* given */
    fakeAirQualityClient.willFailForAllRequests();

    /* when */
    var warsaw = airQualityTool.getCurrentAirQuality("Warsaw");

    /* then */
    assertThat(warsaw.aqi()).isNull();
    assertThat(warsaw.details()).isEqualTo("Air quality data unavailable for Warsaw");
  }

  @Test
  void shouldReturnErrorResponseForUnknownCity() {
    /* when */
    var unknown = airQualityTool.getCurrentAirQuality("Unknown");

    /* then */
    assertThat(unknown.aqi()).isNull();
    assertThat(unknown.details()).isEqualTo("Air quality data unavailable for Unknown");
  }

  @Test
  void shouldReturnAqiForGivenCity() {
    /* when */
    var warsaw = airQualityTool.getCurrentAirQuality("Warsaw");

    /* then */
    assertThat(warsaw.aqi()).isEqualTo(55);
    assertThat(warsaw.details()).isEqualTo("AQI retrieved successfully for Warsaw");
  }
}
