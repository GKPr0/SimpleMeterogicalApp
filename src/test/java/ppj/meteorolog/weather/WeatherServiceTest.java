package ppj.meteorolog.weather;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ppj.meteorolog.city.City;
import ppj.meteorolog.city.CityRepository;
import ppj.meteorolog.shared.Result;

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
        weatherMeasurement.setTemperature(666);
        weatherMeasurement.setPressure(1010);
        weatherMeasurement.setHumidity(79);
        weatherMeasurement.setTimestamp(Instant.now());

        Result<String> addResult = weatherService.addWeatherMeasurementRecord(weatherMeasurement);
        assertTrue(addResult.getIsSuccess());

        Result<WeatherMeasurement> getResult = weatherService.getCurrentWeatherForCity("CZ","Liberec");
        assertTrue(getResult.getIsSuccess());
        assertEquals(666, getResult.getValue().getTemperature());
        assertEquals(1010, getResult.getValue().getPressure());
        assertEquals(79, getResult.getValue().getHumidity());
    }

    @Test
    public void testAddWeatherMeasurementForNonExistentCity() {
        WeatherMeasurement weatherMeasurement = new WeatherMeasurement();
        weatherMeasurement.setCityID(UUID.randomUUID());
        weatherMeasurement.setTemperature(666);
        weatherMeasurement.setPressure(1010);
        weatherMeasurement.setHumidity(79);
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
        weatherMeasurement.setTemperature(666);
        weatherMeasurement.setPressure(1010);
        weatherMeasurement.setHumidity(79);
        weatherMeasurement.setTimestamp(Instant.now());

        Result<String> addResult = weatherService.addWeatherMeasurementRecord(weatherMeasurement);
        assertTrue(addResult.getIsSuccess());

        weatherMeasurement.setHumidity(1000);
        weatherMeasurement.setTemperature(30);
        weatherMeasurement.setPressure(1000);

        Result<String> addResult2 = weatherService.addWeatherMeasurementRecord(weatherMeasurement);
        assertFalse(addResult2.getIsSuccess());
        assertEquals("Measurement for city already exists", addResult2.getError());
    }

    @Test
    public void testDeleteWeatherMeasurementForCity() {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        Instant timestamp = Instant.now();

        WeatherMeasurement weatherMeasurement = new WeatherMeasurement();
        weatherMeasurement.setCityID(city.get().getId());
        weatherMeasurement.setTemperature(666);
        weatherMeasurement.setPressure(1010);
        weatherMeasurement.setHumidity(79);
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
}
