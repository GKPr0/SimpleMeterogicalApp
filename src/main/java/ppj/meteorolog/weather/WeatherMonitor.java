package ppj.meteorolog.weather;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.BucketRetentionRules;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class WeatherMonitor {

    private final InfluxDBClient influxDBClient;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public WeatherMonitor(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    @Scheduled(fixedRate = 5000)
    public void writeTest() {
        int count = (int) (Math.random() * 100);

        Point point = Point
                .measurement("Test")
                .addTag("url", "/hello")
                .addField("count", count)
                .time(Instant.now(), WritePrecision.NS);

        WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
        writeApiBlocking.writePoint(point);
        log.info("New measurement logged with value：" + count);
    }
}
