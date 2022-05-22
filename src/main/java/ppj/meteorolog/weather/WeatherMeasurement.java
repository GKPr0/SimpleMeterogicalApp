package ppj.meteorolog.weather;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.Instant;
import java.util.UUID;

@Measurement(name = "weather")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherMeasurement {
    @Column(timestamp = true)
    @NotNull(message = "Timestamp is required")
    private Instant timestamp;

    @Column(tag = true)
    @NotNull(message = "City ID is required")
    private UUID cityID;

    @Column
    @NotNull(message = "Temperature is required")
    private Double temperature;

    @Column
    @NotNull(message = "Humidity is required")
    @Positive(message = "Humidity must be positive number")
    private double humidity;

    @Column
    @NotNull(message = "Pressure is required")
    @Positive(message = "Pressure must be positive number")
    private double pressure;
}
