package io.github.kmikuta.api;

import io.github.kmikuta.workflows.LocationInfoWorkflow;
import io.github.kmikuta.workflows.OutdoorActivityPlannerWorkflow;
import io.github.kmikuta.workflows.WeatherOrAirQualityWorkflow;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
class WorkflowController {

  private final LocationInfoWorkflow locationInfoWorkflow;
  private final OutdoorActivityPlannerWorkflow outdoorActivityPlannerWorkflow;
  private final WeatherOrAirQualityWorkflow weatherOrAirQualityWorkflow;

  WorkflowController(
      LocationInfoWorkflow locationInfoWorkflow,
      OutdoorActivityPlannerWorkflow outdoorActivityPlannerWorkflow,
      WeatherOrAirQualityWorkflow weatherOrAirQualityWorkflow) {
    this.locationInfoWorkflow = locationInfoWorkflow;
    this.outdoorActivityPlannerWorkflow = outdoorActivityPlannerWorkflow;
    this.weatherOrAirQualityWorkflow = weatherOrAirQualityWorkflow;
  }

  /** Example: GET /api/workflows/location?place=Tokyo */
  @GetMapping("/location")
  String locationInfo(@RequestParam(name = "place") String place) {
    return locationInfoWorkflow.execute(place);
  }

  /** Example: GET /api/workflows/activities?cities=Warsaw,Tokyo */
  @GetMapping("/activities")
  List<String> outdoorActivities(@RequestParam String[] cities) {
    return outdoorActivityPlannerWorkflow.execute(Arrays.asList(cities));
  }

  /** Example: GET /api/workflows/weather-or-air?prompt=What's the weather like in Warsaw */
  @GetMapping("/weather-or-air")
  String weatherOrAirQuality(@RequestParam String prompt) {
    return weatherOrAirQualityWorkflow.execute(prompt);
  }
}
