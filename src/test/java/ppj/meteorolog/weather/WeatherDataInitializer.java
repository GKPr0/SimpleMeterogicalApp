package ppj.meteorolog.weather;

import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ppj.meteorolog.city.City;
import ppj.meteorolog.city.CityRepository;
import ppj.meteorolog.country.Country;
import ppj.meteorolog.country.CountryRepository;
import ppj.meteorolog.db.InfluxDbConfig;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class WeatherDataInitializer {

    @Autowired
    public final CityRepository cityRepository;

    @Autowired
    public final CountryRepository countryRepository;

    @Autowired
    private final InfluxDBClient influxDBClient;

    @Autowired
    private final InfluxDbConfig config;

    public WeatherDataInitializer(CityRepository cityRepository, CountryRepository countryRepository, WeatherRepository weatherRepository, InfluxDBClient influxDBClient, InfluxDbConfig influxDbConfig) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.influxDBClient = influxDBClient;
        this.config = influxDbConfig;
    }

    public void setup(){

        Country cz = new Country("CZ", "Czech Republic");
        Country uk = new Country("UK", "United Kingdom");

        countryRepository.saveAll(
                List.of(cz, uk)
        );

        City liberec = new City("Liberec", cz);
        City london = new City("London", uk);

        cityRepository.saveAll(
                List.of(liberec, london)
        );

        addWeatherMeasurementToCity(liberec, 30, 1000, 80, Instant.now());
        addWeatherMeasurementToCity(liberec, 20, 1000, 80, Instant.now().minus(1, ChronoUnit.HOURS));
        addWeatherMeasurementToCity(liberec, 10, 1000, 80, Instant.now().minus(2, ChronoUnit.HOURS));
        addWeatherMeasurementToCity(liberec, 24, 1000, 80, Instant.now().minus(3, ChronoUnit.DAYS));
        addWeatherMeasurementToCity(liberec, 16, 1000, 80, Instant.now().minus(10, ChronoUnit.DAYS));

    }

    private void addWeatherMeasurementToCity(City city, double temp, double pressure, double humidity, Instant timestamp){
        WeatherMeasurement measurement = new WeatherMeasurement();
        measurement.setTemperature(temp);
        measurement.setPressure(pressure);
        measurement.setHumidity(humidity);
        measurement.setCityID(city.getId());
        measurement.setTimestamp(timestamp);

        WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
        writeApiBlocking.writeMeasurement(WritePrecision.NS, measurement);
    }

    public void clear(){
        cityRepository.deleteAll();
        countryRepository.deleteAll();

        String bucket = config.getBucket();
        String org = config.getOrg();
        String predicate = "_measurement = \"weather\"";
        OffsetDateTime startTime = OffsetDateTime.now().minusYears(10);
        OffsetDateTime endTime = OffsetDateTime.now().plusYears(10);

        DeleteApi deleteApi = influxDBClient.getDeleteApi();
        deleteApi.delete(startTime, endTime, predicate, bucket, org);
    }
}
