package ppj.meteorolog.country;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CountryDataInitializer {

    private final CountryRepository countryRepository;

    public CountryDataInitializer(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public void setup() {

        Country cz = new Country("CZ", "Czech Republic");
        Country uk = new Country("UK", "United Kingdom");
        Country us = new Country("US", "United States");
        Country it = new Country("IT", "Italy");

        countryRepository.saveAll(
                List.of(cz, uk, us, it)
        );
    }

    public void clear() {
        countryRepository.deleteAll();
    }
}
