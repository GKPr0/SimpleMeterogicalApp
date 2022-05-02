package ppj.meteorolog.weather;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class WeatherRepository {

    private final InfluxDBClient influxDBClient;

    @Autowired
    public WeatherRepository(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    public WeatherMeasurement findLastMeasurementForCity(UUID cityId) {
        String query = "from(bucket: \"Development\") " +
                "|> range(start: -1d) " +
                "|> filter(fn: (r) => r._measurement == \"weather\" and r.cityID == \"" + cityId + "\" )" +
                "|> filter(fn: (r) => r._field == \"humidity\" or r._field == \"temperature\" or r._field == \"pressure\")" +
                "|> last() " +
                "|> pivot(rowKey: [\"_stop\"], columnKey: [\"_field\"], valueColumn: \"_value\")";

        List<FluxTable> tables = influxDBClient.getQueryApi().query(query);
        FluxRecord result = tables.get(0).getRecords().get(0);

        return mapFluxRecordToWeatherMeasurement(result, cityId);
    }

    public WeatherMeasurement findLastDayAverageForCity(UUID cityId) {
        return findPeriodAverageForCity("1d", cityId);
    }

    public WeatherMeasurement findLastWeekAverageForCity(UUID cityId) {
        return findPeriodAverageForCity("7d", cityId);
    }

    public WeatherMeasurement findLastTwoWeeksAverageForCity(UUID cityId) {
        return findPeriodAverageForCity("14d", cityId);
    }

    private WeatherMeasurement findPeriodAverageForCity(String period, UUID cityId) {
        String query = "from(bucket: \"Development\") " +
                "|> range(start: -" + period + ") " +
                "|> filter(fn: (r) => r._measurement == \"weather\" and r.cityID == \"" + cityId + "\" )" +
                "|> filter(fn: (r) => r._field == \"humidity\" or r._field == \"temperature\" or r._field == \"pressure\")" +
                "|> mean()" +
                "|> pivot(rowKey: [\"_stop\"], columnKey: [\"_field\"], valueColumn: \"_value\")";

        List<FluxTable> tables = influxDBClient.getQueryApi().query(query);
        FluxRecord result = tables.get(0).getRecords().get(0);

       return mapFluxRecordToWeatherMeasurement(result, cityId);
    }

    private WeatherMeasurement mapFluxRecordToWeatherMeasurement(FluxRecord result, UUID cityId) {
        double temperature = (double) result.getValueByKey("temperature");
        double humidity = (double) result.getValueByKey("humidity");
        double pressure = (double) result.getValueByKey("pressure");

        WeatherMeasurement measurement = new WeatherMeasurement();
        measurement.setCityID(cityId);
        measurement.setHumidity(temperature);
        measurement.setTemperature(humidity);
        measurement.setPressure(pressure);

        return measurement;
    }
}
