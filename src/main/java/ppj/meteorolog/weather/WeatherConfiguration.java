package ppj.meteorolog.weather;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.BucketRetentionRules;
import com.influxdb.exceptions.InfluxException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.boot.autoconfigure.influx.InfluxDbAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "influxdb")
@Getter
@Setter
public class WeatherConfiguration {

    @NotBlank
    private String bucket;

    @NotBlank
    private String org;

    @NotBlank
    private String token;

    @NotBlank
    private String url;

    private Integer retentionPeriod;

    @Bean
    public InfluxDBClient influxDBClient() {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
        updateRetentionPeriod(influxDBClient);
        return influxDBClient;
    }

    private void updateRetentionPeriod(InfluxDBClient influxDBClient) {
        Bucket bucketClient = influxDBClient.getBucketsApi().findBucketByName(bucket);

        if(bucketClient == null)
            throw new InfluxException("Bucket not found");

        BucketRetentionRules bucketRetentionRules = bucketClient.getRetentionRules().get(0);
        if(retentionPeriod < bucketRetentionRules.getShardGroupDurationSeconds() && retentionPeriod !=0)
            throw new InfluxException("Retention period cannot be smaller than shard group duration");

        bucketRetentionRules.setEverySeconds(retentionPeriod);

        influxDBClient.getBucketsApi().updateBucket(bucketClient);
    }
}
