package ppj.meteorolog.city;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ppj.meteorolog.shared.BaseApiController;
import ppj.meteorolog.shared.BlockInReadOnlyMode;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/v1/city")
public class CityController extends BaseApiController {

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<?> getAllCities() {
        return HandleResult(cityService.getAllCities());
    }

    @GetMapping(path = "/{countryCode}")
    public ResponseEntity<?> getAllCitiesInCountry(@PathVariable String countryCode) {
        return HandleResult(cityService.getAllCitiesInCountry(countryCode));
    }

    @GetMapping(path = "/{countryCode}/{cityName}")
    public ResponseEntity<?> getCity(@PathVariable String countryCode, @PathVariable String cityName) {
        return HandleResult(cityService.getCity(countryCode, cityName));
    }

    @PostMapping
    @BlockInReadOnlyMode
    public ResponseEntity<?> createCity(@Valid @RequestBody City city) {
        return HandleResult(cityService.createCity(city));
    }

    @PutMapping(path="/{countryCode}/{cityName}")
    @BlockInReadOnlyMode
    public ResponseEntity<?> updateCity(@PathVariable String countryCode, @PathVariable String cityName, @Valid @RequestBody City city) {
        return HandleResult(cityService.updateCity(countryCode, cityName, city));
    }

    @DeleteMapping(path="/{countryCode}/{cityName}")
    @BlockInReadOnlyMode
    public ResponseEntity<?> deleteCity(@PathVariable String countryCode, @PathVariable String cityName) {
        return HandleResult(cityService.deleteCity(countryCode, cityName));
    }
}

