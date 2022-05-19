package ppj.meteorolog.weather;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ppj.meteorolog.city.City;
import ppj.meteorolog.city.CityRepository;
import ppj.meteorolog.shared.BlockInReadOnlyMode;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@Profile("!test")
public class WeatherMonitor {

    private final WeatherConfig config;
    private final InfluxDBClient influxDBClient;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final CityRepository cityRepository;

    public WeatherMonitor(WeatherConfig config, InfluxDBClient influxDBClient, CityRepository cityRepository) {
        this.config = config;
        this.influxDBClient = influxDBClient;
        this.cityRepository = cityRepository;
    }

    @Scheduled(fixedRateString = "${weather.downloadRate}")
    @BlockInReadOnlyMode
    public void updateWeather() {
        Iterable<City> cities = cityRepository.findAll();

        for (City city : cities) {
            Mono<String> responseBody = requestWeatherDataForCity(city);
            responseBody.subscribe( data -> saveResponseAsWeatherMeasurement(data, city));
        }
    }

    private Mono<String> requestWeatherDataForCity(City city) {
        String requestUrl = config.getDownloadUrl()
                .replace("{city name}", city.getName())
                .replace("{country code}", city.getCountry().getCode())
                .replace("{API key}", config.getApiKey());

        return WebClient.create()
                .get()
                .uri(requestUrl)
                .retrieve()
                .bodyToMono(String.class);
    }

    private void saveResponseAsWeatherMeasurement(String responseBody, City city){
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONObject mainObject = jsonObject.getJSONObject("main");

            double temperature = mainObject.getDouble("temp");
            double pressure = mainObject.getDouble("pressure");
            double humidity = mainObject.getDouble("humidity");
            Instant dateTime = Instant.ofEpochSecond(jsonObject.getLong("dt"));

            WeatherMeasurement measurement = new WeatherMeasurement();
            measurement.setTemperature(temperature);
            measurement.setPressure(pressure);
            measurement.setHumidity(humidity);
            measurement.setCityID(city.getId());
            measurement.setTimestamp(dateTime);

            WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
            writeApiBlocking.writeMeasurement(WritePrecision.NS, measurement);
            log.info("New measurement logged for city：" + city.getName());

        } catch (JSONException e) {
            log.error("Unable to parse JSON to WeatherMeasurement for city: " + city.getName(), e);
        }
    }
}
