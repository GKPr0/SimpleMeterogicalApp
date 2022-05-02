package ppj.meteorolog.weather;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

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

    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }
}
