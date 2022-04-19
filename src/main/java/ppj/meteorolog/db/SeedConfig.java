package ppj.meteorolog.db;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ppj.meteorolog.city.City;
import ppj.meteorolog.city.CityRepository;
import ppj.meteorolog.country.Country;
import ppj.meteorolog.country.CountryRepository;

import java.util.List;

@Configuration
public class SeedConfig {

    @Bean
    CommandLineRunner commandLineRunner(CountryRepository countryRepository, CityRepository cityRepository) {
        return  args -> {
            Country cz = new Country("CZ", "Czech Republic");
            Country uk = new Country("UK", "United Kingdom");
            Country us = new Country("US", "United States");
            Country it = new Country("IT", "Italy");

            countryRepository.saveAll(
                    List.of(cz, uk, us, it)
            );

            City liberec = new City("Liberec", cz);
            City prague = new City("Prague", cz);
            City london = new City("London", uk);
            City cambridge = new City("Cambridge", uk);
            City chicago = new City("Chicago", us);
            City newYork = new City("New York", us);
            City cambridgeUs = new City("Cambridge", us);
            City rome = new City("Rome", it);
            City milan = new City("Milan", it);

            cityRepository.saveAll(
                    List.of(liberec, prague, london, cambridge, chicago, newYork, cambridgeUs, rome, milan)
            );
        };
    }
}
