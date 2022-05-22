package ppj.meteorolog.weather;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class WeatherCsvHelper {

    public static void printToWriter(Writer writer, Iterable<WeatherMeasurement> measurements) throws IOException {
        CSVPrinter printer = new CSVPrinter(writer,
                CSVFormat.DEFAULT.withHeader("Timestamp", "Temperature", "Pressure", "Humidity"));

        for(WeatherMeasurement measurement : measurements){
            printer.printRecord(
                    measurement.getTimestamp(),
                    measurement.getTemperature(),
                    measurement.getPressure(),
                    measurement.getHumidity());
        }
    }

    public static Iterable<WeatherMeasurement> parseFromReader(Reader reader) throws IOException {
        List<WeatherMeasurement> measurements = new ArrayList<>();
        CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());

        for(CSVRecord record : parser){
            WeatherMeasurement measurement = new WeatherMeasurement();
            measurement.setTimestamp(Instant.parse(record.get("Timestamp")));
            measurement.setTemperature(Double.parseDouble(record.get("Temperature")));
            measurement.setPressure(Double.parseDouble(record.get("Pressure")));
            measurement.setHumidity(Double.parseDouble(record.get("Humidity")));
            measurements.add(measurement);
        }

        return measurements;
    }
}
