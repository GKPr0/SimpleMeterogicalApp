package ppj.meteorolog.weather;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Validated
@Configuration
@ConfigurationProperties(prefix = "weather")
@Getter
@Setter
public class WeatherConfig {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String downloadUrl;

    @Min(60000)
    private Integer downloadRate;
}
