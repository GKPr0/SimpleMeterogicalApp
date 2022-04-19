package ppj.meteorolog.city;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, CityId> {
    Iterable<City> findCitiesByCountry_Code(String countryCode);
    Optional<City> findCityByNameAndCountry_Code(String cityName, String countryCode);
}
