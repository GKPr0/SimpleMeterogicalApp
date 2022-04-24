package ppj.meteorolog.city;

import org.springframework.stereotype.Service;
import ppj.meteorolog.country.Country;
import ppj.meteorolog.country.CountryRepository;
import ppj.meteorolog.shared.Result;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
public class CityService {

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public CityService(CityRepository cityRepository, CountryRepository countryRepository) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    public Result<Iterable<City>> getAllCities() {
        return Result.success(cityRepository.findAll());
    }

    public Result<Iterable<City>> getAllCitiesInCountry(String countryCode) {
        Iterable<City> cities = cityRepository.findCitiesByCountry_Code(countryCode);

        if(cities.spliterator().getExactSizeIfKnown() == 0)
            return Result.failure("No cities found in country " + countryCode);

        return Result.success(cities);
    }

    public Result<City> getCity(String countryCode, String cityName) {
       Optional<City> optionalCity = cityRepository.findCityByNameAndCountry_Code(cityName, countryCode);

       if(optionalCity.isEmpty())
           return Result.failure("City with name " + cityName + " not found in country " + countryCode);

       return Result.success(optionalCity.get());
    }

    public Result<String> createCity(City city) {
        String cityName = city.getName();
        String countryCode = city.getCountry().getCode();

        Optional<Country> optionalCountry = countryRepository.findByCode(countryCode);

        if(optionalCountry.isEmpty())
            return Result.failure("Cannot add city to non existent country " + countryCode);

        Optional<City> optionalCity =  cityRepository.findCityByNameAndCountry_Code(cityName, countryCode);

        if(optionalCity.isPresent())
            return Result.failure("City with name " + cityName + " already exists in country " + countryCode);

        city.setCountry(optionalCountry.get());
        cityRepository.save(city);

        return Result.success("City " + cityName + " created in country " + countryCode);
    }

    @Transactional
    public Result<String> updateCity(String countryCode, String cityName, City updatedCity) {
        Optional<City> optionalCity = cityRepository.findCityByNameAndCountry_Code(cityName, countryCode);

        if(optionalCity.isEmpty())
            return Result.failure("City with name " + cityName + " not found in country " + countryCode);

        City city = optionalCity.get();
        String updatedCityName = updatedCity.getName();
        String updatedCountryCode = updatedCity.getCountry().getCode();

        Optional<Country> optionalCountry = countryRepository.findByCode(updatedCountryCode);

        if(optionalCountry.isEmpty())
            return Result.failure("Cannot move city to non existent country " + updatedCountryCode);

        Optional<City> optionalUpdatedCity = cityRepository
                .findCityByNameAndCountry_Code(updatedCityName, updatedCountryCode);

        if(optionalUpdatedCity.isPresent())
            return Result.failure("City with name " + updatedCityName + " already exists in country " + updatedCountryCode);

        city.setName(updatedCityName);
        city.setCountry(optionalCountry.get());

        return Result.success("City " + cityName + " updated in country " + countryCode);
    }

    public Result<String> deleteCity(String countryCode, String cityName) {
        Optional<City> optionalCity = cityRepository.findCityByNameAndCountry_Code(cityName, countryCode);

        if(optionalCity.isEmpty())
            return Result.failure("City with name " + cityName + " not found in country " + countryCode);

        cityRepository.delete(optionalCity.get());

        return Result.success("City " + cityName + " deleted from country " + countryCode);
    }
}
