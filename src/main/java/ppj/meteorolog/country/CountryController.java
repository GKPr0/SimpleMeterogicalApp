package ppj.meteorolog.country;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/country")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    public Iterable<Country> getAllCountries() {
        return countryService.getAllCountries();
    }

    @GetMapping(path = "/{countryCode}")
    public Country getCountry(@PathVariable String countryCode) {
        return countryService.getCountry(countryCode);
    }

    @PostMapping
    public void createCountry(@RequestBody Country country) {
        countryService.createCountry(country);
    }

    @PutMapping(path = "/{countryCode}")
    public void updateCountry(@PathVariable String countryCode, @RequestBody Country country) {
        countryService.updateCountry(countryCode, country);
    }

    @DeleteMapping(path = "/{countryCode}")
    public void deleteCountry(@PathVariable String countryCode) {
        countryService.deleteCountry(countryCode);
    }
}
