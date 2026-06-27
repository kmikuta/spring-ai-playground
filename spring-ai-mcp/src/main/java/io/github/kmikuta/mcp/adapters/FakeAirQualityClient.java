package io.github.kmikuta.mcp.adapters;

import io.github.kmikuta.mcp.domain.AirQuality;
import io.github.kmikuta.mcp.domain.AirQualityClient;
import io.github.kmikuta.mcp.domain.Geocoding;
import io.github.kmikuta.mcp.domain.Location;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import tools.jackson.databind.ObjectMapper;

@Component
@Profile("fakeApi")
public class FakeAirQualityClient implements AirQualityClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(FakeAirQualityClient.class);

  private static final String GEOCODING_PATTERN =
      "classpath:io/github/kmikuta/mcp/adapters/geocoding/*.json";

  private final ResourcePatternResolver resourcePatternResolver;
  private final ObjectMapper objectMapper;
  private final Map<Location, AirQuality> store = new HashMap<>();

  private boolean willFailForAllRequests = false;

  public FakeAirQualityClient(
      ResourcePatternResolver resourcePatternResolver, ObjectMapper objectMapper) {
    this.resourcePatternResolver = resourcePatternResolver;
    this.objectMapper = objectMapper;
  }

  @PostConstruct
  public void loadResources() throws IOException {
    for (Resource geocodingResource : resourcePatternResolver.getResources(GEOCODING_PATTERN)) {
      String slug = geocodingResource.getFilename().replace(".json", "");
      LOGGER.info("Loading air quality resource for slug: {}", slug);

      Geocoding geocoding;
      try (var inputStream = geocodingResource.getInputStream()) {
        geocoding = objectMapper.readValue(inputStream, Geocoding.class);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }

      var airQualityResource =
          new ClassPathResource("io/github/kmikuta/mcp/adapters/air_quality/" + slug + ".json");
      try (var inputStream = airQualityResource.getInputStream()) {
        store.put(Location.from(geocoding), objectMapper.readValue(inputStream, AirQuality.class));
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  @Override
  public Optional<AirQuality> getCurrentAirQuality(Location location) {
    if (willFailForAllRequests) {
      throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return Optional.ofNullable(store.get(location));
  }

  public void willFailForAllRequests() {
    willFailForAllRequests = true;
  }

  public void reset() {
    willFailForAllRequests = false;
  }
}
