package ppj.meteorolog.city;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public Iterable<City> getAllCities() {
        return cityRepository.findAll();
    }

    public Iterable<City> getAllCitiesInCountry(String countryCode) {
        return cityRepository.findCitiesByCountry_Code(countryCode);
    }

    public City getCity(String countryCode, String cityName) {
       return cityRepository.findCityByNameAndCountry_Code(cityName, countryCode)
               .orElseThrow(() -> new IllegalAccessError("City not found"));
    }

    public void createCity(City city) {
        Optional<City> cityInDb =  cityRepository.findCityByNameAndCountry_Code(city.getCountry().getCode(), city.getName());

        if(cityInDb.isPresent()) {
            throw new IllegalAccessError("City already exists");
        }

        cityRepository.save(city);
    }

    @Transactional
    public void updateCity(String countryCode, String cityName, City updatedCity) {
        City city = cityRepository.findCityByNameAndCountry_Code(cityName, countryCode)
                .orElseThrow(() -> new IllegalAccessError("City not found"));

        //TODO check if values from updated city can be assigned to City

        city.setName(updatedCity.getName());
        city.setCountry(updatedCity.getCountry());
        city.setLatitude(updatedCity.getLatitude());
        city.setLongitude(updatedCity.getLongitude());
    }

    public void deleteCity(String countryCode, String cityName) {
        cityRepository.findCityByNameAndCountry_Code(cityName, countryCode)
            .ifPresentOrElse(cityRepository::delete,
            () -> { throw new IllegalAccessError("City not found"); });
    }
}
