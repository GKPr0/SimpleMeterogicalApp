package ppj.meteorolog.weather;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ppj.meteorolog.shared.BaseApiController;
import ppj.meteorolog.shared.BlockInReadOnlyMode;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/v1/weather")
public class WeatherController extends BaseApiController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current/{countryCode}/{cityName}")
    public ResponseEntity<?> getCurrentWeatherForCity(@PathVariable String countryCode, @PathVariable String cityName) {
        return HandleResult(weatherService.getCurrentWeatherForCity(countryCode, cityName));
    }

    @GetMapping("/average/1d/{countryCode}/{cityName}")
    public ResponseEntity<?> getLastDayAverageWeatherForCity(@PathVariable String countryCode, @PathVariable String cityName) {
        return HandleResult(weatherService.getLastDayAverageWeatherForCity(countryCode, cityName));
    }

    @GetMapping("/average/1w/{countryCode}/{cityName}")
    public ResponseEntity<?> getLastWeekAverageWeatherForCity(@PathVariable String countryCode, @PathVariable String cityName) {
        return HandleResult(weatherService.getLastWeekAverageWeatherForCity(countryCode, cityName));
    }

    @GetMapping("/average/2w/{countryCode}/{cityName}")
    public ResponseEntity<?> getLastTwoWeeksAverageWeatherForCity(@PathVariable String countryCode, @PathVariable String cityName) {
        return HandleResult(weatherService.getLastTwoWeeksAverageWeatherForCity(countryCode, cityName));
    }

    @PostMapping
    @BlockInReadOnlyMode
    public ResponseEntity<?> addWeatherMeasurementRecord(@Valid @RequestBody WeatherMeasurement weather) {
        return HandleResult(weatherService.addWeatherMeasurementRecord(weather));
    }

    @DeleteMapping("/{countryCode}/{cityName}/{timestamp}")
    @BlockInReadOnlyMode
    public ResponseEntity<?> deleteWeatherMeasurementRecord(@PathVariable String countryCode, @PathVariable String cityName, @PathVariable String timestamp) {
        return HandleResult(weatherService.deleteWeatherMeasurementRecord(countryCode, cityName, timestamp));
    }
}
