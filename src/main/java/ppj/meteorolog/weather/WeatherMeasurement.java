package ppj.meteorolog.weather;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Measurement(name = "weather")
@Getter
@Setter
@NoArgsConstructor
public class WeatherMeasurement {
    @Column(timestamp = true)
    private Instant timestamp;

    @Column(tag = true)
    private UUID cityID;

    @Column
    private double temperature;

    @Column
    private double humidity;

    @Column
    private double pressure;
}
