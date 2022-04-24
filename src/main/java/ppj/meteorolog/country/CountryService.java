package ppj.meteorolog.country;

import org.springframework.stereotype.Service;
import ppj.meteorolog.country.exceptions.CountryAlreadyExistsException;
import ppj.meteorolog.country.exceptions.CountryNotFoundException;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Iterable<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public Country getCountry(String countryCode) {
        return countryRepository.findByCode(countryCode)
                .orElseThrow((() -> new CountryNotFoundException(countryCode)));
    }

    public void createCountry(Country country) {
        Optional<Country> countryInDb = countryRepository.findByCode(country.getCode());

        if (countryInDb.isPresent()) {
            throw new CountryAlreadyExistsException(country.getCode());
        }

        countryRepository.save(country);
    }

    @Transactional
    public void updateCountry(String countryCode, Country updatedCountry) {
        Country country = countryRepository.findByCode(countryCode)
                .orElseThrow((() -> new CountryNotFoundException(countryCode)));

        // TODO check if values are valid

        country.setName(updatedCountry.getName());
        country.setCode(updatedCountry.getCode());
    }

    public void deleteCountry(String countryCode) {
        countryRepository.findByCode(countryCode)
            .ifPresentOrElse(countryRepository::delete,
            () -> { throw new CountryNotFoundException(countryCode); });
    }
}
