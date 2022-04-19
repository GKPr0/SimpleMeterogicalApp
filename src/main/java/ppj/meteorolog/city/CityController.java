package ppj.meteorolog.city;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/city")
public class CityController {

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
    public void createCity(@RequestBody City city) {
        cityService.createCity(city);
    }

    @PutMapping(path="/{countryCode}/{cityName}")
    public void updateCity(@PathVariable String countryCode, @PathVariable String cityName, @RequestBody City city) {
        cityService.updateCity(countryCode, cityName, city);
    }

    @DeleteMapping(path="/{countryCode}/{cityName}")
    public void deleteCity(@PathVariable String countryCode, @PathVariable String cityName) {
        cityService.deleteCity(countryCode, cityName);
    }
}

