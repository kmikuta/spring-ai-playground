package io.github.kmikuta.api;

import io.github.kmikuta.workflows.LocationInfoWorkflow;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
class WorkflowController {

  private final LocationInfoWorkflow locationInfoWorkflow;

  WorkflowController(LocationInfoWorkflow locationInfoWorkflow) {
    this.locationInfoWorkflow = locationInfoWorkflow;
  }

  @GetMapping("/location")
  String locationInfo(@RequestParam(name = "place") String place) {
    return locationInfoWorkflow.execute(place);
  }
}
