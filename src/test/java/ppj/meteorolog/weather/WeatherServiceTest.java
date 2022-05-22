package ppj.meteorolog.weather;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ppj.meteorolog.city.City;
import ppj.meteorolog.city.CityRepository;
import ppj.meteorolog.db.InfluxDbConfig;
import ppj.meteorolog.shared.Result;

import java.io.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class WeatherServiceTest {

    @Autowired
    private WeatherDataInitializer dataInitializer;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private InfluxDbConfig dbConfig;

    @BeforeEach
    public void setup() {
        dataInitializer.setup();
    }

    @AfterEach
    public void clean() {
        dataInitializer.clear();
    }

    @Test
    public void testGetCurrentWeatherForCity() {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        Result<WeatherMeasurement> getResult = weatherService.getCurrentWeatherForCity("CZ","Liberec");

        assertTrue(getResult.getIsSuccess());
        assertEquals(30, getResult.getValue().getTemperature());
        assertEquals(1000, getResult.getValue().getPressure());
        assertEquals(80, getResult.getValue().getHumidity());
    }

    @Test
    public void testGetCurrentWeatherForNonExistentCity() {
        Result<WeatherMeasurement> getResult = weatherService.getCurrentWeatherForCity("CZ","NonExistentCity");

        assertNull(getResult);
    }

    @Test
    public void testGetCurrentWeatherForNonExistentCountry() {
        Result<WeatherMeasurement> getResult = weatherService.getCurrentWeatherForCity("NonExistentCountry","Liberec");

        assertNull(getResult);
    }

    @Test
    public void testGetCurrentWeatherForCityWithNoWeatherRecord() {
        Result<WeatherMeasurement> getResult = weatherService.getCurrentWeatherForCity("UK","London");

        assertNull(getResult);
    }

    @Test
    public void testGetLastDayAverageWeatherForCity() {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        Result<WeatherMeasurement> getResult = weatherService.getLastDayAverageWeatherForCity("CZ","Liberec");

        assertTrue(getResult.getIsSuccess());
        assertEquals(20, getResult.getValue().getTemperature());
        assertEquals(1000, getResult.getValue().getPressure());
        assertEquals(80, getResult.getValue().getHumidity());
    }

    @Test
    public void testGetLastDayAverageWeatherForNonExistentCity() {
        Result<WeatherMeasurement> getResult = weatherService.getLastDayAverageWeatherForCity("CZ","NonExistentCity");

        assertNull(getResult);
    }

    @Test
    public void testGetLastDayAverageWeatherForNonExistentCountry() {
        Result<WeatherMeasurement> getResult = weatherService.getLastDayAverageWeatherForCity("NonExistentCountry","Liberec");

        assertNull(getResult);
    }

    @Test
    public void testGetLastDayAverageWeatherForCityWithNoWeatherRecord() {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        Result<WeatherMeasurement> getResult = weatherService.getLastDayAverageWeatherForCity("UK","London");

        assertNull(getResult);
    }

    @Test
    public void testGetLastWeekAverageWeatherForCity() {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        Result<WeatherMeasurement> getResult = weatherService.getLastWeekAverageWeatherForCity("CZ","Liberec");

        assertTrue(getResult.getIsSuccess());
        assertEquals(21, getResult.getValue().getTemperature());
        assertEquals(1000, getResult.getValue().getPressure());
        assertEquals(80, getResult.getValue().getHumidity());
    }

    @Test
    public void testGetLastWeekAverageWeatherForNonExistentCity() {
        Result<WeatherMeasurement> getResult = weatherService.getLastWeekAverageWeatherForCity("CZ","NonExistentCity");

        assertNull(getResult);
    }

    @Test
    public void testGetLastWeekAverageWeatherForNonExistentCountry() {
        Result<WeatherMeasurement> getResult = weatherService.getLastWeekAverageWeatherForCity("NonExistentCountry","Liberec");

        assertNull(getResult);
    }

    @Test
    public void testGetLastWeekAverageWeatherForCityWithNoWeatherRecord() {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        Result<WeatherMeasurement> getResult = weatherService.getLastWeekAverageWeatherForCity("UK","London");

        assertNull(getResult);
    }

    @Test
    public void testGetLastTwoWeeksAverageWeatherForCity() {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        Result<WeatherMeasurement> getResult = weatherService.getLastTwoWeeksAverageWeatherForCity("CZ","Liberec");

        assertTrue(getResult.getIsSuccess());
        assertEquals(20, getResult.getValue().getTemperature());
        assertEquals(1000, getResult.getValue().getPressure());
        assertEquals(80, getResult.getValue().getHumidity());
    }

    @Test
    public void testGetLastTwoWeeksAverageWeatherForNonExistentCity() {
        Result<WeatherMeasurement> getResult = weatherService.getLastTwoWeeksAverageWeatherForCity("CZ","NonExistentCity");

        assertNull(getResult);
    }

    @Test
    public void testGetLastTwoWeeksAverageWeatherForNonExistentCountry() {
        Result<WeatherMeasurement> getResult = weatherService.getLastTwoWeeksAverageWeatherForCity("NonExistentCountry","Liberec");

        assertNull(getResult);
    }

    @Test
    public void testGetLastTwoWeeksAverageWeatherForCityWithNoWeatherRecord() {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        Result<WeatherMeasurement> getResult = weatherService.getLastTwoWeeksAverageWeatherForCity("UK","London");

        assertNull(getResult);
    }

    @Test
    public void testAddWeatherMeasurementForCity() {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        WeatherMeasurement weatherMeasurement = new WeatherMeasurement();
        weatherMeasurement.setCityID(city.get().getId());
        weatherMeasurement.setTemperature(666.0);
        weatherMeasurement.setPressure(1010.0);
        weatherMeasurement.setHumidity(79.0);
        weatherMeasurement.setTimestamp(Instant.now());

        Result<String> addResult = weatherService.addWeatherMeasurementRecord(weatherMeasurement);
        assertTrue(addResult.getIsSuccess());

        Result<WeatherMeasurement> getResult = weatherService.getCurrentWeatherForCity("CZ","Liberec");
        assertTrue(getResult.getIsSuccess());
        assertEquals(666.0, getResult.getValue().getTemperature());
        assertEquals(1010.0, getResult.getValue().getPressure());
        assertEquals(79.0, getResult.getValue().getHumidity());
    }

    @Test
    public void testAddWeatherMeasurementForNonExistentCity() {
        WeatherMeasurement weatherMeasurement = new WeatherMeasurement();
        weatherMeasurement.setCityID(UUID.randomUUID());
        weatherMeasurement.setTemperature(666.0);
        weatherMeasurement.setPressure(1010.0);
        weatherMeasurement.setHumidity(79.0);
        weatherMeasurement.setTimestamp(Instant.now());

        Result<String> addResult = weatherService.addWeatherMeasurementRecord(weatherMeasurement);
        assertFalse(addResult.getIsSuccess());
        assertEquals("Cannot add measurement to non existent city", addResult.getError());
    }

    @Test
    public void testAddWeatherMeasurementForCityWithTimestampThatAlreadyExists(){
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        WeatherMeasurement weatherMeasurement = new WeatherMeasurement();
        weatherMeasurement.setCityID(city.get().getId());
        weatherMeasurement.setTemperature(666.0);
        weatherMeasurement.setPressure(1010.0);
        weatherMeasurement.setHumidity(79.0);
        weatherMeasurement.setTimestamp(Instant.now());

        Result<String> addResult = weatherService.addWeatherMeasurementRecord(weatherMeasurement);
        assertTrue(addResult.getIsSuccess());

        weatherMeasurement.setHumidity(1000.0);
        weatherMeasurement.setTemperature(30.0);
        weatherMeasurement.setPressure(1000.0);

        Result<String> addResult2 = weatherService.addWeatherMeasurementRecord(weatherMeasurement);
        assertFalse(addResult2.getIsSuccess());
        assertEquals("Measurement for city already exists", addResult2.getError());
    }

    @Test
    public void testAddWeatherMeasurementForCityOlderThenRetentionPeriod(){
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        WeatherMeasurement weatherMeasurement = new WeatherMeasurement();
        weatherMeasurement.setCityID(city.get().getId());
        weatherMeasurement.setTemperature(666.0);
        weatherMeasurement.setPressure(1010.0);
        weatherMeasurement.setHumidity(79.0);
        weatherMeasurement.setTimestamp(Instant.now().minusSeconds(dbConfig.getRetentionPeriod() + 1));

        Result<String> addResult = weatherService.addWeatherMeasurementRecord(weatherMeasurement);
        assertFalse(addResult.getIsSuccess());
        assertEquals("Measurement is too old", addResult.getError());
    }

    @Test
    public void testDeleteWeatherMeasurementForCity() {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        Instant timestamp = Instant.now();

        WeatherMeasurement weatherMeasurement = new WeatherMeasurement();
        weatherMeasurement.setCityID(city.get().getId());
        weatherMeasurement.setTemperature(666.0);
        weatherMeasurement.setPressure(1010.0);
        weatherMeasurement.setHumidity(79.0);
        weatherMeasurement.setTimestamp(timestamp);

        Result<String> addResult = weatherService.addWeatherMeasurementRecord(weatherMeasurement);
        assertTrue(addResult.getIsSuccess());

        Result<String> deleteResult = weatherService.deleteWeatherMeasurementRecord("CZ", "Liberec", timestamp.toString());
        assertTrue(deleteResult.getIsSuccess());

        Optional<WeatherMeasurement> getResult = weatherRepository.findMeasurementForCityByTimestamp(weatherMeasurement.getCityID(), timestamp);
        assertTrue(getResult.isEmpty());
    }

    @Test
    public void testDeleteWeatherMeasurementForNonExistentCity() {
        Instant timestamp = Instant.now();

        Result<String> deleteResult = weatherService.deleteWeatherMeasurementRecord("CZ", "NonExistentCity", timestamp.toString());
        assertNull(deleteResult);
    }

    @Test
    public void testDeleteWeatherMeasurementForNonExistentCountry() {
        Instant timestamp = Instant.now();

        Result<String> deleteResult = weatherService.deleteWeatherMeasurementRecord("NonExistentCountry", "Liberec", timestamp.toString());
        assertNull(deleteResult);
    }

    @Test
    public void testDeleteNonExistentWeatherMeasurementForCity() {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        Instant timestamp = Instant.now();

        Result<String> deleteResult = weatherService.deleteWeatherMeasurementRecord("CZ", "Liberec", timestamp.toString());
        assertNull(deleteResult);
    }

    @Test
    public void testDeleteWeatherMeasurementWithInvalidTimestampFormat() {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        Result<String> deleteResult = weatherService.deleteWeatherMeasurementRecord("CZ", "Liberec", "InvalidTimestamp");
        assertFalse(deleteResult.getIsSuccess());
        assertEquals("Invalid timestamp", deleteResult.getError());
    }

    @Test
    public void testExportWeatherMeasurements() throws IOException {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now();

        weatherRepository.save(new WeatherMeasurement(timestamp, cityId, 20.0, 80.0, 1000.0));

        Writer writer = new StringWriter();
        Result<String> exportResult = weatherService.exportWeatherMeasurementsForCityToCsv("UK", "London", writer);
        assertTrue(exportResult.getIsSuccess());

        String csv = writer.toString();
        assertEquals("Timestamp,Temperature,Pressure,Humidity\r\n" +
                timestamp.toString() + ",20.0,1000.0,80.0\r\n", csv);
    }

    @Test
    public void testExportWeatherMeasurementsForNonExistentCity() throws IOException {
        Writer writer = new StringWriter();
        Result<String> exportResult = weatherService.exportWeatherMeasurementsForCityToCsv("UK", "NonExistentCity", writer);
        assertNull(exportResult);
    }

    @Test
    public void testExportWeatherMeasurementsForNonExistentCountry() throws IOException {
        Writer writer = new StringWriter();
        Result<String> exportResult = weatherService.exportWeatherMeasurementsForCityToCsv("NonExistentCountry", "London", writer);
        assertNull(exportResult);
    }

    @Test
    public void testExportWeatherMeasurementsForCityWithEmptyCsv() throws IOException {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        Writer writer = new StringWriter();
        Result<String> exportResult = weatherService.exportWeatherMeasurementsForCityToCsv("UK", "London", writer);
        assertTrue(exportResult.getIsSuccess());

        String csv = writer.toString();
        assertEquals("Timestamp,Temperature,Pressure,Humidity\r\n", csv);
    }

    @Test
    public void testImportWeatherMeasurements() throws IOException {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now();

        String csv = "Timestamp,Temperature,Pressure,Humidity\r\n" +
                timestamp.toString() + ",20.0,1000.0,80.0\r\n";

        Reader reader = new StringReader(csv);

        Result<String> importResult = weatherService.importWeatherMeasurementsForCityFromCsv("UK", "London", reader);
        assertTrue(importResult.getIsSuccess());

        Optional<WeatherMeasurement> weatherMeasurement = weatherRepository.findMeasurementForCityByTimestamp(cityId, timestamp);
        assertTrue(weatherMeasurement.isPresent());
        assertEquals(20.0, weatherMeasurement.get().getTemperature());
        assertEquals(1000.0, weatherMeasurement.get().getPressure());
        assertEquals(80.0, weatherMeasurement.get().getHumidity());
    }

    @Test
    public void testImportWeatherMeasurementsForNonExistentCity() throws IOException {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        Instant timestamp = Instant.now();

        String csv = "Timestamp,Temperature,Pressure,Humidity\r\n" +
                timestamp.toString() + ",20.0,1000.0,80.0\r\n";

        Reader reader = new StringReader(csv);

        Result<String> importResult = weatherService.importWeatherMeasurementsForCityFromCsv("UK", "NonExistentCity", reader);
        assertNull(importResult);
    }

    @Test
    public void testImportWeatherMeasurementsForNonExistentCountry() throws IOException {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        Instant timestamp = Instant.now();

        String csv = "Timestamp,Temperature,Pressure,Humidity\r\n" +
                timestamp.toString() + ",20.0,1000.0,80.0\r\n";

        Reader reader = new StringReader(csv);

        Result<String> importResult = weatherService.importWeatherMeasurementsForCityFromCsv("NonExistentCountry", "London", reader);
        assertNull(importResult);
    }

    @Test
    public void testImportWeatherMeasurementWithInvalidHeader() throws IOException {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now();

        String csv = "Timestamp,TEMPERATURETURE,Pressure,Humidity\r\n" +
                timestamp.toString() + ",20.0,1000.0,80.0\r\n";

        Reader reader = new StringReader(csv);

        Result<String> importResult = weatherService.importWeatherMeasurementsForCityFromCsv("UK", "London", reader);
        assertFalse(importResult.getIsSuccess());
        assertEquals("Unable to parse CSV", importResult.getError());
    }

    @Test
    public void testImportWeatherMeasurementWithInvalidData() throws IOException {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now();

        String csv = "Timestamp,Temperature,Pressure,Humidity\r\n" +
                timestamp.toString() + ",hello,1000.0,80.0\r\n";

        Reader reader = new StringReader(csv);

        Result<String> importResult = weatherService.importWeatherMeasurementsForCityFromCsv("UK", "London", reader);
        assertFalse(importResult.getIsSuccess());
        assertEquals("Unable to parse CSV", importResult.getError());
    }
}
