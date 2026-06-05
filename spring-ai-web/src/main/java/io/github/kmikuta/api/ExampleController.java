package io.github.kmikuta.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class ExampleController {

  @GetMapping("/hello")
  Hello sayHello() {
    return new Hello("Hello World!");
  }

  record Hello(String message) {}
}
