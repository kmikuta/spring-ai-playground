package io.github.kmikuta.api;

import io.github.kmikuta.workflows.LocationInfoWorkflow;
import io.github.kmikuta.workflows.OutdoorActivityPlannerWorkflow;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
class WorkflowController {

  private final LocationInfoWorkflow locationInfoWorkflow;
  private final OutdoorActivityPlannerWorkflow outdoorActivityPlannerWorkflow;

  WorkflowController(
      LocationInfoWorkflow locationInfoWorkflow,
      OutdoorActivityPlannerWorkflow outdoorActivityPlannerWorkflow) {
    this.locationInfoWorkflow = locationInfoWorkflow;
    this.outdoorActivityPlannerWorkflow = outdoorActivityPlannerWorkflow;
  }

  @GetMapping("/location")
  String locationInfo(@RequestParam(name = "place") String place) {
    return locationInfoWorkflow.execute(place);
  }

  @GetMapping("/outdoor-activities")
  String outdoorActivities(@RequestParam(name = "city") String city) {
    return outdoorActivityPlannerWorkflow.execute(city);
  }
}
