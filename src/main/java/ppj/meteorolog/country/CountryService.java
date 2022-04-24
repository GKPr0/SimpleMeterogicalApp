package ppj.meteorolog.country;

import org.springframework.stereotype.Service;
import ppj.meteorolog.shared.Result;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Result<Iterable<Country>> getAllCountries() {
        return Result.success(countryRepository.findAll());
    }

    public Result<Country> getCountry(String countryCode) {
        Optional<Country> optionalCountry = countryRepository.findByCode(countryCode);

        if(optionalCountry.isEmpty())
            return Result.failure("Country with code " + countryCode + " not found");

        return Result.success(optionalCountry.get());
    }

    public Result<String> createCountry(Country country) {
        String countryCode = country.getCode();

        Optional<Country> optionalCountry = countryRepository.findByCode(countryCode);

        if (optionalCountry.isPresent())
            return Result.failure("Country with code " + countryCode + " already exists");

        countryRepository.save(country);

        return Result.success("Country " + countryCode + " created");
    }

    @Transactional
    public Result<String> updateCountry(String countryCode, Country updatedCountry) {
        Optional<Country> optionalCountry = countryRepository.findByCode(countryCode);

        if(optionalCountry.isEmpty())
            return Result.failure("Country with code " + countryCode + " not found");

        Country country = optionalCountry.get();
        country.setName(updatedCountry.getName());
        country.setCode(updatedCountry.getCode());

        return Result.success("Country " + countryCode + " updated");
    }

    public Result<String> deleteCountry(String countryCode) {
        Optional<Country> optionalCountry = countryRepository.findByCode(countryCode);

        if(optionalCountry.isEmpty())
            return Result.failure("Country with code " + countryCode + " not found");

        countryRepository.delete(optionalCountry.get());

        return Result.success("Country " + countryCode + " deleted");
    }
}
