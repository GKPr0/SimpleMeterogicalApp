package ppj.meteorolog.db;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.Bucket;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import ppj.meteorolog.db.exceptions.BucketNotFoundException;
import ppj.meteorolog.db.exceptions.RetentionPeriodException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Validated
@Configuration
@ConfigurationProperties(prefix = "influxdb")
@Getter
@Setter
public class InfluxDbConfig {

    @NotBlank
    private String bucket;

    @NotBlank
    private String org;

    @NotBlank
    private String token;

    @NotBlank
    private String url;

    @Min(3600)
    private Integer retentionPeriod;

    @Min(3600)
    private Long shardGroupDuration;

    @Bean
    public InfluxDBClient influxDBClient() {
        InfluxDBClient influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
        updateRetentionPeriod(influxDBClient);
        return influxDBClient;
    }

    private void updateRetentionPeriod(InfluxDBClient influxDBClient) {
        Bucket bucketClient = influxDBClient.getBucketsApi().findBucketByName(bucket);

        if(bucketClient == null)
            throw new BucketNotFoundException(bucket);

        if(retentionPeriod !=0 && retentionPeriod < shardGroupDuration)
            throw new RetentionPeriodException(retentionPeriod, shardGroupDuration);

        bucketClient
                .getRetentionRules()
                .get(0)
                .everySeconds(retentionPeriod)
                .shardGroupDurationSeconds(shardGroupDuration);

        influxDBClient.getBucketsApi().updateBucket(bucketClient);
    }
}