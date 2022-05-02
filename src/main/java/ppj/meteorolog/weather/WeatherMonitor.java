package ppj.meteorolog.weather;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class WeatherMonitor {

    private final InfluxDBClient influxDBClient;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final UUID id = UUID.randomUUID();
    private final WeatherRepository weatherRepository;

    @Autowired
    public WeatherMonitor(InfluxDBClient influxDBClient, WeatherRepository weatherRepository) {
        this.influxDBClient = influxDBClient;
        this.weatherRepository = weatherRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void writeTest() {
        int count = (int) (Math.random() * 100);

        WeatherMeasurement measurement = new WeatherMeasurement();
        measurement.setTemperature(Math.random() * 40);
        measurement.setPressure(Math.random() * 300 + 800);
        measurement.setHumidity(Math.random() * 100);
        measurement.setCityID(id);
        measurement.setTimestamp(Instant.now());

        WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
        writeApiBlocking.writeMeasurement(WritePrecision.NS, measurement);
        log.info("New measurement logged with value：" + count);

        WeatherMeasurement weatherMeasurement = weatherRepository.findLastMeasurementForCity(id);
    }
}
