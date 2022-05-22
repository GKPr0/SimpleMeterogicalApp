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
    @NotNull
    private Instant timestamp;

    @Column(tag = true)
    @NotNull
    private UUID cityID;

    @Column
    @NotNull
    private Double temperature;

    @Column
    @NotNull
    @Positive
    private double humidity;

    @Column
    @NotNull
    @Positive
    private double pressure;
}
