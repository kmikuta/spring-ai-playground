package io.github.kmikuta.mcp.adapters;

import io.github.kmikuta.mcp.domain.Geocoding;
import io.github.kmikuta.mcp.domain.GeocodingClient;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import tools.jackson.databind.ObjectMapper;

@Component
@Profile("fakeApi")
public class FakeGeocodingClient implements GeocodingClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(FakeGeocodingClient.class);

  private static final String PATTERN = "classpath:io/github/kmikuta/mcp/adapters/geocoding/*.json";

  private final ResourcePatternResolver resourcePatternResolver;
  private final ObjectMapper objectMapper;
  private final Map<String, Geocoding> store = new HashMap<>();

  private boolean willFailForAllRequests = false;

  public FakeGeocodingClient(
      ResourcePatternResolver resourcePatternResolver, ObjectMapper objectMapper) {
    this.resourcePatternResolver = resourcePatternResolver;
    this.objectMapper = objectMapper;
  }

  @PostConstruct
  public void loadResources() throws IOException {
    for (Resource resource : resourcePatternResolver.getResources(PATTERN)) {
      LOGGER.info("Loading geocoding resource: {}", resource.getFilename());
      try (var inputStream = resource.getInputStream()) {
        Geocoding geocoding = objectMapper.readValue(inputStream, Geocoding.class);
        store.put(geocoding.name(), geocoding);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  @Override
  public Optional<Geocoding> getGeocoding(String city) {
    if (willFailForAllRequests) {
      throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return Optional.ofNullable(store.get(city));
  }

  public void willFailForAllRequests() {
    willFailForAllRequests = true;
  }

  public void reset() {
    willFailForAllRequests = false;
  }
}
