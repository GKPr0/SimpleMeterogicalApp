package ppj.meteorolog.country;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ppj.meteorolog.shared.BaseApiController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/v1/country")
public class CountryController extends BaseApiController {

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
    public ResponseEntity<String> createCountry(@Valid @RequestBody Country country) {
         countryService.createCountry(country);
         return ResponseEntity.ok("Country created");
    }

    @PutMapping(path = "/{countryCode}")
    public ResponseEntity<String> updateCountry(@PathVariable String countryCode, @RequestBody Country country) {
        countryService.updateCountry(countryCode, country);
        return ResponseEntity.ok("Country updated");
    }

    @DeleteMapping(path = "/{countryCode}")
    public ResponseEntity<String> deleteCountry(@PathVariable String countryCode) {
        countryService.deleteCountry(countryCode);
        return ResponseEntity.ok("Country deleted");
    }
}
