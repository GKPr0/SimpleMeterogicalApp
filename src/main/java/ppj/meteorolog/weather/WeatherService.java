package ppj.meteorolog.weather;

import org.springframework.stereotype.Service;
import ppj.meteorolog.city.City;
import ppj.meteorolog.city.CityRepository;
import ppj.meteorolog.shared.Result;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class WeatherService {

    private final CityRepository cityRepository;
    private final WeatherRepository weatherRepository;

    public WeatherService(CityRepository cityRepository, WeatherRepository weatherRepository) {
        this.cityRepository = cityRepository;
        this.weatherRepository = weatherRepository;
    }

    public Result<WeatherMeasurement> getCurrentWeatherForCity(String countryCode, String cityName) {
        return getWeatherMeasurementForCity(countryCode, cityName, weatherRepository::findLastMeasurementForCity);
    }

    public Result<WeatherMeasurement> getLastDayAverageWeatherForCity(String countryCode, String cityName) {
        return getWeatherMeasurementForCity(countryCode, cityName, weatherRepository::findLastDayAverageForCity);
    }

    public Result<WeatherMeasurement> getLastWeekAverageWeatherForCity(String countryCode, String cityName) {
        return getWeatherMeasurementForCity(countryCode, cityName, weatherRepository::findLastWeekAverageForCity);
    }

    public Result<WeatherMeasurement> getLastTwoWeeksAverageWeatherForCity(String countryCode, String cityName) {
        return getWeatherMeasurementForCity(countryCode, cityName, weatherRepository::findLastTwoWeeksAverageForCity);
    }

    private Result<WeatherMeasurement> getWeatherMeasurementForCity(String countryCode,
                                                                    String cityName,
                                                                    Function<UUID, Optional<WeatherMeasurement>> action) {
        Optional<City> optionalCity = cityRepository.findCityByNameAndCountry_Code (cityName, countryCode);

        if(optionalCity.isEmpty())
            return null;

        UUID cityId = optionalCity.get().getId();
        Optional<WeatherMeasurement> optionalWeatherMeasurement = action.apply(cityId);

        if(optionalWeatherMeasurement.isEmpty())
            return null;

        return Result.success(optionalWeatherMeasurement.get());
    }

    public Result<String> addWeatherMeasurementRecord(WeatherMeasurement weather) {
        UUID cityId = weather.getCityID();
        Optional<City> optionalCity = cityRepository.findById(cityId);

        if(optionalCity.isEmpty())
            return Result.failure("Cannot add measurement to non existent city");

        Optional<WeatherMeasurement> optionalMeasurement = weatherRepository
                .findMeasurementForCityByTimestamp(weather.getCityID(), weather.getTimestamp());

        if(optionalMeasurement.isPresent())
            return Result.failure("Measurement for city already exists");

        weatherRepository.save(weather);

        return Result.success("Weather record added");
    }

    public Result<String> deleteWeatherMeasurementRecord(String countryCode, String cityName, String timestamp) {
        try {
            Instant timestampInstant = Instant.parse(timestamp);

            Optional<City> optionalCity = cityRepository.findCityByNameAndCountry_Code(cityName, countryCode);

            if (optionalCity.isEmpty())
                return null;

            UUID cityId = optionalCity.get().getId();

            Optional<WeatherMeasurement> optionalMeasurement = weatherRepository
                    .findMeasurementForCityByTimestamp(cityId, timestampInstant);

            if(optionalMeasurement.isEmpty())
                return null;

            weatherRepository.delete(optionalMeasurement.get());

            return Result.success("Measurement deleted");

        } catch (DateTimeParseException e) {
            return Result.failure("Invalid timestamp");
        }
    }
}