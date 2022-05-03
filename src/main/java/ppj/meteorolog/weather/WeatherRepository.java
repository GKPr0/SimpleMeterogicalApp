package ppj.meteorolog.weather;

import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WeatherRepository {

    private final InfluxDBClient influxDBClient;

    public WeatherRepository(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    public void save(WeatherMeasurement measurement) {
        WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
        writeApiBlocking.writeMeasurement(WritePrecision.NS, measurement);
    }

    public void delete(WeatherMeasurement measurement) {
        String bucket = "Development";
        String org = "Development";
        String predicate = "_measurement = \"weather\" and cityID = \"" + measurement.getCityID() + "\"";
        OffsetDateTime startTime = measurement.getTimestamp().atOffset(ZoneOffset.UTC);
        OffsetDateTime endTime = startTime.plusNanos(1);

        DeleteApi deleteApi = influxDBClient.getDeleteApi();
        deleteApi.delete(startTime, endTime, predicate, bucket, org);
    }

    public Optional<WeatherMeasurement> findMeasurementForCityByTimestamp(UUID cityId, Instant timestamp) {
        String query = "from(bucket: \"Development\")" +
                "  |> range(start: " + timestamp + ", stop: " + timestamp.plusNanos(1) + ")" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \"weather\")" +
                "  |> filter(fn: (r) => r[\"_field\"] == \"humidity\" or r[\"_field\"] == \"pressure\" or r[\"_field\"] == \"temperature\")" +
                "  |> filter(fn: (r) => r[\"cityID\"] == \"" + cityId + "\")" +
                "  |> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")";

        List<FluxTable> tables = influxDBClient.getQueryApi().query(query);
        if(tables.size() == 0)
            return Optional.empty();

        FluxRecord result = tables.get(0).getRecords().get(0);

        return mapFluxRecordToWeatherMeasurement(result, cityId);
    }

    public Optional<WeatherMeasurement> findLastMeasurementForCity(UUID cityId) {
        String query = "from(bucket: \"Development\") " +
                "|> range(start: -1d) " +
                "|> filter(fn: (r) => r._measurement == \"weather\" and r.cityID == \"" + cityId + "\" )" +
                "|> filter(fn: (r) => r._field == \"humidity\" or r._field == \"temperature\" or r._field == \"pressure\")" +
                "|> last() " +
                "|> pivot(rowKey: [\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")";

        List<FluxTable> tables = influxDBClient.getQueryApi().query(query);
        if(tables.size() == 0)
            return Optional.empty();

        FluxRecord result = tables.get(0).getRecords().get(0);

        return mapFluxRecordToWeatherMeasurement(result, cityId);
    }

    public Optional<WeatherMeasurement> findLastDayAverageForCity(UUID cityId) {
        return findPeriodAverageForCity("1d", cityId);
    }

    public Optional<WeatherMeasurement> findLastWeekAverageForCity(UUID cityId) {
        return findPeriodAverageForCity("7d", cityId);
    }

    public Optional<WeatherMeasurement> findLastTwoWeeksAverageForCity(UUID cityId) {
        return findPeriodAverageForCity("14d", cityId);
    }

    private Optional<WeatherMeasurement> findPeriodAverageForCity(String period, UUID cityId) {
        String query = "from(bucket: \"Development\") " +
                "|> range(start: -" + period + ") " +
                "|> filter(fn: (r) => r._measurement == \"weather\" and r.cityID == \"" + cityId + "\" )" +
                "|> filter(fn: (r) => r._field == \"humidity\" or r._field == \"temperature\" or r._field == \"pressure\")" +
                "|> mean()" +
                "|> pivot(rowKey: [\"_stop\"], columnKey: [\"_field\"], valueColumn: \"_value\")";

        List<FluxTable> tables = influxDBClient.getQueryApi().query(query);
        if(tables.size() == 0)
            return Optional.empty();

        FluxRecord result = tables.get(0).getRecords().get(0);

        return mapFluxRecordToWeatherMeasurement(result, cityId);
    }

    private Optional<WeatherMeasurement> mapFluxRecordToWeatherMeasurement(FluxRecord result, UUID cityId) {
        if(result == null)
            return Optional.empty();

        double temperature = (double) result.getValueByKey("temperature");
        double humidity = (double) result.getValueByKey("humidity");
        double pressure = (double) result.getValueByKey("pressure");
        Instant timestamp = result.getTime();

        WeatherMeasurement measurement = new WeatherMeasurement();
        measurement.setCityID(cityId);
        measurement.setTimestamp(timestamp);
        measurement.setHumidity(temperature);
        measurement.setTemperature(humidity);
        measurement.setPressure(pressure);

        return Optional.of(measurement);
    }
}
