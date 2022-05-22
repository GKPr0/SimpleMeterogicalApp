package ppj.meteorolog.weather;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ppj.meteorolog.Application;
import ppj.meteorolog.city.City;
import ppj.meteorolog.city.CityRepository;
import ppj.meteorolog.db.InfluxDbConfig;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WeatherControllerTest {

    @Autowired
    private WeatherDataInitializer dataInitializer;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private InfluxDbConfig dbConfig;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        dataInitializer.setup();
    }

    @AfterEach
    public void clean() {
        dataInitializer.clear();
    }

    @Test
    public void testGetCurrentWeatherForCity_thenStatus200() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();

        mvc.perform(get("/api/v1/weather/current/CZ/Liberec"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.cityID").value(cityId.toString()))
            .andExpect(jsonPath("$.temperature").value(30.0))
            .andExpect(jsonPath("$.humidity").value(80.0))
            .andExpect(jsonPath("$.pressure").value(1000.0));
    }

    @Test
    public void testGetCurrentWeatherForNonExistentCity_thenStatus404() throws Exception {
        mvc.perform(get("/api/v1/weather/current/CZ/NonExistentCity"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCurrentWeatherForNonExistentCountry_thenStatus404() throws Exception {
        mvc.perform(get("/api/v1/weather/current/NonExistentCountry/Liberec"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCurrentWeatherForCityWithNoWeatherRecord_thenStatus404() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        mvc.perform(get("/api/v1/weather/current/UK/London"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetLastDayAverageWeatherForCity_thenStatus200() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();

        mvc.perform(get("/api/v1/weather/average/1d/CZ/Liberec"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.cityID").value(cityId.toString()))
            .andExpect(jsonPath("$.temperature").value(20.0))
            .andExpect(jsonPath("$.humidity").value(80.0))
            .andExpect(jsonPath("$.pressure").value(1000.0));
    }

    @Test
    public void testGetLastDayAverageWeatherForNonExistentCity_thenStatus404() throws Exception {
        mvc.perform(get("/api/v1/weather/average/1d/CZ/NonExistentCity"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetLastDayAverageWeatherForNonExistentCountry_thenStatus404() throws Exception {
        mvc.perform(get("/api/v1/weather/average/1d/NonExistentCountry/Liberec"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetLastDayAverageWeatherForCityWithNoWeatherRecord_thenStatus404() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        mvc.perform(get("/api/v1/weather/average/1d/UK/London"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetLastWeekAverageWeatherForCity_thenStatus200() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();

        mvc.perform(get("/api/v1/weather/average/1w/CZ/Liberec"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.cityID").value(cityId.toString()))
            .andExpect(jsonPath("$.temperature").value(21.0))
            .andExpect(jsonPath("$.humidity").value(80.0))
            .andExpect(jsonPath("$.pressure").value(1000.0));
    }

    @Test
    public void testGetLastWeekAverageWeatherForNonExistentCity_thenStatus404() throws Exception {
        mvc.perform(get("/api/v1/weather/average/1w/CZ/NonExistentCity"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetLastWeekAverageWeatherForNonExistentCountry_thenStatus404() throws Exception {
        mvc.perform(get("/api/v1/weather/average/1w/NonExistentCountry/Liberec"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetLastWeekAverageWeatherForCityWithNoWeatherRecord_thenStatus404() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        mvc.perform(get("/api/v1/weather/average/1w/UK/London"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetLastTwoWeeksAverageWeatherForCity_thenStatus200() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();

        mvc.perform(get("/api/v1/weather/average/2w/CZ/Liberec"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.cityID").value(cityId.toString()))
            .andExpect(jsonPath("$.temperature").value(20.0))
            .andExpect(jsonPath("$.humidity").value(80.0))
            .andExpect(jsonPath("$.pressure").value(1000.0));
    }

    @Test
    public void testGetLastTwoWeeksAverageWeatherForNonExistentCity_thenStatus404() throws Exception {
        mvc.perform(get("/api/v1/weather/average/2w/CZ/NonExistentCity"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetLastTwoWeeksAverageWeatherForNonExistentCountry_thenStatus404() throws Exception {
        mvc.perform(get("/api/v1/weather/average/2w/NonExistentCountry/Liberec"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetLastTwoWeeksAverageWeatherForCityWithNoWeatherRecord_thenStatus404() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        mvc.perform(get("/api/v1/weather/average/2w/UK/London"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void tesAddWeatherMeasurementForCity_thenStatus200() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now();

        mvc.perform(post("/api/v1/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cityID\":\"" + cityId + "\"," +
                        "\"timestamp\":\"" + timestamp + "\"," +
                        "\"temperature\":666.5," +
                        "\"humidity\":79.0," +
                        "\"pressure\":1010.0}"))
            .andExpect(status().isOk());

        Optional<WeatherMeasurement> createdMeasurement = weatherRepository.findMeasurementForCityByTimestamp(cityId, timestamp);
        assertTrue(createdMeasurement.isPresent());
        assertEquals(666.5, createdMeasurement.get().getTemperature());
        assertEquals(79.0, createdMeasurement.get().getHumidity());
        assertEquals(1010.0, createdMeasurement.get().getPressure());
    }

    @Test
    public void testAddWeatherMeasurementForNonExistentCity_thenStatus400() throws Exception {
        Instant timestamp = Instant.now();

        mvc.perform(post("/api/v1/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cityID\":\"" + UUID.randomUUID() + "\"," +
                        "\"timestamp\":\"" + timestamp + "\"," +
                        "\"temperature\":666.5," +
                        "\"humidity\":79.0," +
                        "\"pressure\":1010.0}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddWeatherMeasurementForCityWithTimestampThatAlreadyExists_thenStatus400() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now();

        weatherRepository.save(new WeatherMeasurement(timestamp, cityId, 20.0, 80.0, 1000.0));

        mvc.perform(post("/api/v1/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cityID\":\"" + cityId + "\"," +
                        "\"timestamp\":\"" + timestamp + "\"," +
                        "\"temperature\":666.5," +
                        "\"humidity\":79.0," +
                        "\"pressure\":1010.0}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddWeatherMeasurementForCityOlderThenRetentionPeriod_thenStatus400() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now().minusSeconds(dbConfig.getRetentionPeriod() + 1);

        mvc.perform(post("/api/v1/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cityID\":\"" + cityId + "\"," +
                        "\"timestamp\":\"" + timestamp + "\"," +
                        "\"temperature\":666.5," +
                        "\"humidity\":79.0," +
                        "\"pressure\":1010.0}"))
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "\"cityID\":\"\",",
        "\"cityID\":null,",
        "\"cityID\":\"blabla\",",
        "\"cityID\":10,"
    })
    public void testAddWeatherMeasurementForCityWithInvalidCityId_thenStatus400(String cityIdContentBody) throws Exception {
        Instant timestamp = Instant.now();

        mvc.perform(post("/api/v1/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" + cityIdContentBody +
                        "\"timestamp\":\"" + timestamp + "\"," +
                        "\"temperature\":666.5," +
                        "\"humidity\":79.0," +
                        "\"pressure\":1010.0}"))
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "\"timestamp\":\"\",",
        "\"timestamp\":null,",
        "\"timestamp\":\"blabla\","
    })
    public void testAddWeatherMeasurementForCityWithInvalidTimestamp_thenStatus400(String timestampContentBody) throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();

        mvc.perform(post("/api/v1/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cityID\":\"" + cityId + "\"," +
                        timestampContentBody +
                        "\"temperature\":666.5," +
                        "\"humidity\":79.0," +
                        "\"pressure\":1010.0}"))
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "\"temperature\":\"\",",
        "\"temperature\":null,",
        "\"temperature\":\"blabla\","
    })
    public void testAddWeatherMeasurementForCityWithInvalidTemperature_thenStatus400(String temperatureContentBody) throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now();

        mvc.perform(post("/api/v1/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cityID\":\"" + cityId + "\"," +
                        "\"timestamp\":\"" + timestamp + "\"," +
                        temperatureContentBody +
                        "\"humidity\":79.0," +
                        "\"pressure\":1010.0}"))
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "\"pressure\":\"\",",
        "\"pressure\":null,",
        "\"pressure\":\"blabla\",",
        "\"pressure\":-1.0,"
    })
    public void testAddWeatherMeasurementForCityWithInvalidPressure_thenStatus400(String pressureContentBody) throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now();

        mvc.perform(post("/api/v1/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cityID\":\"" + cityId + "\"," +
                        "\"timestamp\":\"" + timestamp + "\"," +
                        "\"temperature\":666.5," +
                        pressureContentBody +
                        "\"humidity\":79.0}"))
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "\"humidity\":\"\",",
        "\"humidity\":null,",
        "\"humidity\":\"blabla\",",
        "\"humidity\":-1.0,"
    })
    public void testAddWeatherMeasurementForCityWithInvalidHumidity_thenStatus400(String humidityContentBody) throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now();

        mvc.perform(post("/api/v1/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cityID\":\"" + cityId + "\"," +
                        "\"timestamp\":\"" + timestamp + "\"," +
                        "\"temperature\":666.5," +
                        humidityContentBody +
                        "\"pressure\":1010.0}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddWeatherMeasurementForCityWithEmptyBody_thenStatus400() throws Exception {
          mvc.perform(post("/api/v1/weather")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteWeatherMeasurement_thenStatus200() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now();

        weatherRepository.save(new WeatherMeasurement(timestamp, cityId, 20.0, 80.0, 1000.0));

        mvc.perform(delete("/api/v1/weather/CZ/Liberec/" + timestamp))
            .andExpect(status().isOk());

        Optional<WeatherMeasurement> deletedMeasurement = weatherRepository.findMeasurementForCityByTimestamp(cityId, timestamp);
        assertTrue(deletedMeasurement.isEmpty());
    }

    @Test
    public void testDeleteWeatherMeasurementForNonExistentCity_thenStatus404() throws Exception {
        Instant timestamp = Instant.now();

        mvc.perform(delete("/api/v1/weather/CZ/NonExistentCity/" + timestamp))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteWeatherMeasurementForNonExistentCountry_thenStatus404() throws Exception {
        Instant timestamp = Instant.now();

        mvc.perform(delete("/api/v1/weather/NonExistentCountry/Liberec/" + timestamp))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteNonExistentWeatherMeasurementForCity_thenStatus404() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        Instant timestamp = Instant.now();

        mvc.perform(delete("/api/v1/weather/CZ/Liberec/" + timestamp))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteWeatherMeasurementWithInvalidTimestampFormat_thenStatus400() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(city.isPresent());

        mvc.perform(delete("/api/v1/weather/CZ/Liberec/invalidTimestamp"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testExportWeatherMeasurements_thenStatus200() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now();

        weatherRepository.save(new WeatherMeasurement(timestamp, cityId, 20.0, 80.0, 1000.0));

        mvc.perform(get("/api/v1/weather/export/UK/London"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"weather_UK_London.csv\""))
            .andExpect(content().string(
                    "Timestamp,Temperature,Pressure,Humidity\r\n" +
                    timestamp.toString() + ",20.0,1000.0,80.0\r\n"));
    }

    @Test
    public void testExportWeatherMeasurementsForCityWithEmptyCsv_thenStatus200() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        mvc.perform(get("/api/v1/weather/export/UK/London"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"weather_UK_London.csv\""))
                .andExpect(content().string("Timestamp,Temperature,Pressure,Humidity\r\n"));
    }

    @Test
    public void testExportWeatherMeasurementsForNonExistentCity_thenStatus404() throws Exception {
        mvc.perform(get("/api/v1/weather/export/UK/NonExistentCity"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testExportWeatherMeasurementsForNonExistentCountry_thenStatus404() throws Exception {
        mvc.perform(get("/api/v1/weather/export/NonExistentCountry/London"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testImportWeatherMeasurement_thenStatus200() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        UUID cityId = city.get().getId();
        Instant timestamp = Instant.now();

        mvc.perform(post("/api/v1/weather/import/UK/London")
                .contentType(MediaType.TEXT_PLAIN)
                .content("Timestamp,Temperature,Pressure,Humidity\n" +
                        timestamp.toString() + ",20.0,1000.0,80.0\n"))
            .andExpect(status().isOk());

        Optional<WeatherMeasurement> importedMeasurement = weatherRepository.findMeasurementForCityByTimestamp(cityId, timestamp);
        assertTrue(importedMeasurement.isPresent());
        assertEquals(20.0, importedMeasurement.get().getTemperature());
        assertEquals(1000.0, importedMeasurement.get().getPressure());
        assertEquals(80.0, importedMeasurement.get().getHumidity());
    }

    @Test
    public void testImportWeatherMeasurementForNonExistentCity_thenStatus404() throws Exception {
        mvc.perform(post("/api/v1/weather/import/UK/NonExistentCity")
                .contentType(MediaType.TEXT_PLAIN)
                .content("Timestamp,Temperature,Pressure,Humidity\n" +
                        Instant.now().toString() + ",20.0,1000.0,80.0\n"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testImportWeatherMeasurementForNonExistentCountry_thenStatus404() throws Exception {
        mvc.perform(post("/api/v1/weather/import/NonExistentCountry/London")
                .contentType(MediaType.TEXT_PLAIN)
                .content("Timestamp,Temperature,Pressure,Humidity\n" +
                        Instant.now().toString() + ",20.0,1000.0,80.0\n"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testImportWeatherMeasurementWithInvalidHeader_thenStatus400() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        Instant timestamp = Instant.now();

        mvc.perform(post("/api/v1/weather/import/UK/London")
                .contentType(MediaType.TEXT_PLAIN)
                .content("Timestamp,TEMPERATURETURE,Pressure,Humidity\n" +
                        timestamp.toString() + ",20.0,1000.0,80.0\n"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Unable to parse CSV"));
    }

    @Test
    public void testImportWeatherMeasurementWithInvalidData_thenStatus400() throws Exception {
        Optional<City> city = cityRepository.findCityByNameAndCountry_Code("London", "UK");
        assertTrue(city.isPresent());

        Instant timestamp = Instant.now();

        mvc.perform(post("/api/v1/weather/import/UK/London")
                .contentType(MediaType.TEXT_PLAIN)
                .content("Timestamp,Temperature,Pressure,Humidity\n" +
                        timestamp.toString() + ",twenty,1000.0,80.0\n"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Unable to parse CSV"));
    }
}
