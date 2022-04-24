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
    public ResponseEntity<?> getAllCountries() {
        return HandleResult(countryService.getAllCountries());
    }

    @GetMapping(path = "/{countryCode}")
    public ResponseEntity<?> getCountry(@PathVariable String countryCode) {
        return HandleResult(countryService.getCountry(countryCode));
    }

    @PostMapping
    public ResponseEntity<?> createCountry(@Valid @RequestBody Country country) {
         return HandleResult(countryService.createCountry(country));
    }

    @PutMapping(path = "/{countryCode}")
    public ResponseEntity<?> updateCountry(@PathVariable String countryCode, @Valid @RequestBody Country country) {
        return HandleResult(countryService.updateCountry(countryCode, country));
    }

    @DeleteMapping(path = "/{countryCode}")
    public ResponseEntity<?> deleteCountry(@PathVariable String countryCode) {
        return HandleResult(countryService.deleteCountry(countryCode));
    }
}
