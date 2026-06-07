package io.github.kmikuta.adapters;

import io.github.kmikuta.tools.CatFactsClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CatFactsNinjaClient implements CatFactsClient {
  private final RestClient restClient = RestClient.create();

  @Override
  public String getRandomFact() {
    CatFactsResponse response =
        restClient.get().uri("https://catfact.ninja/fact").retrieve().body(CatFactsResponse.class);

    return response != null ? response.fact() : null;
  }

  record CatFactsResponse(String fact, int length) {}
}
