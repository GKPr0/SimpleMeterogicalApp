package ppj.meteorolog.weather;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ppj.meteorolog.shared.BaseApiController;
import ppj.meteorolog.shared.BlockInReadOnlyMode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

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

    @PostMapping(path = "/import/{countryCode}/{cityName}")
    public ResponseEntity<?> importWeatherMeasurementsForCityFromCsv(@PathVariable String countryCode, @PathVariable String cityName, HttpServletRequest request) throws IOException {
        return HandleResult(weatherService.importWeatherMeasurementsForCityFromCsv(countryCode, cityName, request.getReader()));
    }

    @GetMapping(path = "/export/{countryCode}/{cityName}")
    public void getAllWeatherMeasurementsForCityInCsv(@PathVariable String countryCode, @PathVariable String cityName, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"weather_" + countryCode + "_" + cityName + ".csv\"");

        ResponseEntity<?> responseEntity = HandleResult(weatherService.exportWeatherMeasurementsForCityToCsv(countryCode, cityName, response.getWriter()));
        response.setStatus(responseEntity.getStatusCodeValue());
    }
}
