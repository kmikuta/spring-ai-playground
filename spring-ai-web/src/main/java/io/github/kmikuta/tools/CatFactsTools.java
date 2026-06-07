package io.github.kmikuta.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class CatFactsTools {
  private static final Logger LOGGER = LoggerFactory.getLogger(CatFactsTools.class);

  private final CatFactsClient catFactsClient;

  public CatFactsTools(CatFactsClient catFactsClient) {
    this.catFactsClient = catFactsClient;
  }

  @Tool(description = "Get a random fun fact about cats")
  public String getRandomCatFact() {
    LOGGER.info("Using tool: CatFactsTools::getRandomCatFact");

    String fact = catFactsClient.getRandomFact();
    if (fact == null) {
      return "Cat fact unavailable";
    }

    return fact;
  }
}
