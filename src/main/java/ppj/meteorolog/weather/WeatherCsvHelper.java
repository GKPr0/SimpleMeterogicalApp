package ppj.meteorolog.weather;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;


public class WeatherCsvHelper {

    public static void PrintToCsvWriter(Writer writer, Iterable<WeatherMeasurement> measurements) throws IOException {
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
        printer.printRecord("Timestamp", "Temperature", "Pressure", "Humidity");
        for(WeatherMeasurement measurement : measurements){
            printer.printRecord(
                    measurement.getTimestamp(),
                    measurement.getTemperature(),
                    measurement.getPressure(),
                    measurement.getHumidity());
        }
    }
}
