package ppj.meteorolog.country;

import ppj.meteorolog.country.Country;
import ppj.meteorolog.country.CountryRepository;

import java.util.List;

public class CountryDataInitializer {

    public static void seed(CountryRepository countryRepository) {

        Country cz = new Country("CZ", "Czech Republic");
        Country uk = new Country("UK", "United Kingdom");
        Country us = new Country("US", "United States");
        Country it = new Country("IT", "Italy");

        countryRepository.saveAll(
                List.of(cz, uk, us, it)
        );
    }
}
