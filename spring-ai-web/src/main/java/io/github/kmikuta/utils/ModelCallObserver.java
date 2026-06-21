package io.github.kmikuta.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class ModelCallObserver {
  private final List<Consumer<String>> handlers = new LinkedList<>();

  public void addObserver(Consumer<String> handler) {
    handlers.add(handler);
  }

  public void notifyObservers(String response) {
    for (Consumer<String> handler : handlers) {
      handler.accept(response);
    }
  }

  public void removeObservers() {
    handlers.clear();
  }
}
