package ppj.meteorolog.city;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ppj.meteorolog.shared.BaseApiController;

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
    public Iterable<City> getAllCities() {
        return cityService.getAllCities();
    }

    @GetMapping(path = "/{countryCode}")
    public Iterable<City> getAllCitiesInCountry(@PathVariable String countryCode) {
        return cityService.getAllCitiesInCountry(countryCode);
    }

    @GetMapping(path = "/{countryCode}/{cityName}")
    public City getCity(@PathVariable String countryCode, @PathVariable String cityName) {
        return cityService.getCity(countryCode, cityName);
    }

    @PostMapping
    public ResponseEntity<String> createCity(@Valid @RequestBody City city) {
        cityService.createCity(city);
        return ResponseEntity.ok("City created");
    }

    @PutMapping(path="/{countryCode}/{cityName}")
    public ResponseEntity<String> updateCity(@PathVariable String countryCode, @PathVariable String cityName, @RequestBody City city) {
        cityService.updateCity(countryCode, cityName, city);
        return ResponseEntity.ok("City updated");
    }

    @DeleteMapping(path="/{countryCode}/{cityName}")
    public ResponseEntity<String> deleteCity(@PathVariable String countryCode, @PathVariable String cityName) {
        cityService.deleteCity(countryCode, cityName);
        return ResponseEntity.ok("City deleted");
    }
}

