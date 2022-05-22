package ppj.meteorolog.weather;

import org.springframework.stereotype.Service;
import ppj.meteorolog.city.City;
import ppj.meteorolog.city.CityRepository;
import ppj.meteorolog.db.InfluxDbConfig;
import ppj.meteorolog.shared.Result;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class WeatherService {

    private final CityRepository cityRepository;
    private final WeatherRepository weatherRepository;
    private final InfluxDbConfig influxDbConfig;

    public WeatherService(CityRepository cityRepository, WeatherRepository weatherRepository, InfluxDbConfig influxDbConfig) {
        this.cityRepository = cityRepository;
        this.weatherRepository = weatherRepository;
        this.influxDbConfig = influxDbConfig;
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
        Instant maxMeasurementOldness = Instant.now().minusSeconds(influxDbConfig.getRetentionPeriod());
        if(weather.getTimestamp().isBefore(maxMeasurementOldness))
            return Result.failure("Measurement is too old");

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

    public Result<String> writeWeatherMeasurementsForCityToCsv(String countryCode, String cityName, Writer writer) throws IOException {
        Optional<City> optionalCity = cityRepository.findCityByNameAndCountry_Code(cityName, countryCode);

        if (optionalCity.isEmpty())
            return null;

        UUID cityId = optionalCity.get().getId();
        Iterable<WeatherMeasurement> measurements = weatherRepository.findAllMeasurementsForCity(cityId);

        WeatherCsvHelper.PrintToWriter(writer, measurements);
        return Result.success("Measurements written to CSV");
    }

    public Result<String> importWeatherMeasurementsForCityFromCsv(String countryCode, String cityName, Reader reader) throws IOException {
        Optional<City> optionalCity = cityRepository.findCityByNameAndCountry_Code(cityName, countryCode);

        if (optionalCity.isEmpty())
            return null;

        UUID cityId = optionalCity.get().getId();
        Iterable<WeatherMeasurement> measurements = WeatherCsvHelper.ParseFromReader(reader);

        for(WeatherMeasurement measurement : measurements) {
            measurement.setCityID(cityId);
            weatherRepository.save(measurement);
        }

        return Result.success("Measurements added");
    }
}
