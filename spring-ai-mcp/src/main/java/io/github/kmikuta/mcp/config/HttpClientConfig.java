package io.github.kmikuta.mcp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class HttpClientConfig {

  @Bean
  RestClient restClient() {
    return RestClient.create();
  }
}
