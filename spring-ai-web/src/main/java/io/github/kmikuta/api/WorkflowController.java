package io.github.kmikuta.api;

import io.github.kmikuta.workflows.LocationInfoWorkflow;
import io.github.kmikuta.workflows.LocationOrActivityWorkflow;
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
  private final LocationOrActivityWorkflow locationOrActivityWorkflow;

  WorkflowController(
      LocationInfoWorkflow locationInfoWorkflow,
      OutdoorActivityPlannerWorkflow outdoorActivityPlannerWorkflow,
      LocationOrActivityWorkflow locationOrActivityWorkflow) {
    this.locationInfoWorkflow = locationInfoWorkflow;
    this.outdoorActivityPlannerWorkflow = outdoorActivityPlannerWorkflow;
    this.locationOrActivityWorkflow = locationOrActivityWorkflow;
  }

  /** Example: GET /api/workflows/location?place=Tokyo */
  @GetMapping("/location")
  String locationInfo(@RequestParam(name = "place") String place) {
    return locationInfoWorkflow.execute(place);
  }

  /** Example: GET /api/workflows/outdoor-activities?city=Warsaw */
  @GetMapping("/outdoor-activities")
  String outdoorActivities(@RequestParam(name = "city") String city) {
    return outdoorActivityPlannerWorkflow.execute(city);
  }

  /**
   * Example (weather info): GET /api/workflows/location-or-activity?prompt=What is the weather in
   * Tokyo Example (outdoor activities): GET /api/workflows/location-or-activity?prompt=Suggest
   * outdoor activities in Warsaw
   */
  @GetMapping("/location-or-activity")
  String locationOrActivity(@RequestParam(name = "prompt") String prompt) {
    return locationOrActivityWorkflow.execute(prompt);
  }
}
