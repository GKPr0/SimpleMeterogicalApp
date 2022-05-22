package ppj.meteorolog.city;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ppj.meteorolog.country.Country;
import ppj.meteorolog.shared.Result;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class CityServiceTest {

    @Autowired
    private CityDataInitializer dataInitializer;

    @Autowired
    private CityService cityService;

    @BeforeEach
    public void setup() {
        dataInitializer.setup();
    }

    @AfterEach
    public void clean() {
        dataInitializer.clear();
    }

    @Test
    public void testGetAllCities() {
        Result<Iterable<City>> getResult = cityService.getAllCities();

        assertTrue(getResult.getIsSuccess());
        assertEquals(9, getResult.getValue().spliterator().getExactSizeIfKnown());
    }

    @Test
    public void testGetAllCitiesFromCountry() {
        Result<Iterable<City>> getResult = cityService.getAllCitiesInCountry("CZ");

        assertTrue(getResult.getIsSuccess());
        assertEquals(2, getResult.getValue().spliterator().getExactSizeIfKnown());
    }

    @Test
    public void testGetAllCitiesFromNonExistentCountry() {
        Result<Iterable<City>> getResult = cityService.getAllCitiesInCountry("Cze");

        assertNull(getResult);
    }

    @Test
    public void testGetCity() {
        Result<City> getResult = cityService.getCity("CZ", "Prague");

        assertTrue(getResult.getIsSuccess());
        assertEquals("Prague", getResult.getValue().getName());
        assertEquals("CZ", getResult.getValue().getCountry().getCode());
    }

    @Test
    public void testGetNonExistentCity() {
        Result<City> getResult = cityService.getCity("CZ", "NonExistentCity");

        assertNull(getResult);
    }

    @Test
    public void testGetCityFromNonExistentCountry() {
        Result<City> getResult = cityService.getCity("NonExistentCountry", "Prague");

        assertNull(getResult);
    }

    @Test
    public void testCreateCity() {
        Country targetCountry = new Country("CZ", "Czech Republic");
        City city = new City("Brno", targetCountry);

        Result<?> createResult = cityService.createCity(city);
        assertTrue(createResult.getIsSuccess());

        Result<City> getResult = cityService.getCity("CZ", "Brno");
        assertTrue(getResult.getIsSuccess());
        assertEquals("Brno", getResult.getValue().getName());
        assertEquals("CZ", getResult.getValue().getCountry().getCode());
    }

    @Test
    public void testCreateAlreadyExistingCityInSpecificCountry() {
        Country targetCountry = new Country("CZ", "Czech Republic");
        City city = new City("Prague", targetCountry);

        Result<?> createResult = cityService.createCity(city);
        assertFalse(createResult.getIsSuccess());
        assertEquals("City with name Prague already exists in country CZ", createResult.getError());
    }

    @Test
    public void testCreateCityInNonExistentCountry() {
        Country targetCountry = new Country("Cze", "Czech Republic");
        City city = new City("Brno", targetCountry);

        Result<?> createResult = cityService.createCity(city);
        assertFalse(createResult.getIsSuccess());
        assertEquals("Cannot add city to non existent country Cze", createResult.getError());
    }

    @Test
    public void testCreateCityWhenMaxCityCountHasBeenReached(){
        dataInitializer.clear();

        Country country = new Country("CZ", "Czech Republic");
        dataInitializer.countryRepository.save(country);

        for (int i = 0; i < 60; i++) {
            City city = new City("Test city " + i, country);
            dataInitializer.cityRepository.save(city);
        }

        City city = new City("Liberec", country);
        Result<?> createResult = cityService.createCity(city);
        assertFalse(createResult.getIsSuccess());
        assertEquals("Cannot create more than 60 cities", createResult.getError());
    }

    @Test
    public void testUpdateCityName() {
        Country targetCountry = new Country("CZ", "Czech Republic");
        City city = new City("Liberec (updated)", targetCountry);

        Result<?> updateResult = cityService.updateCity("CZ", "Liberec", city);
        assertTrue(updateResult.getIsSuccess());

        Result<City> getResult = cityService.getCity("CZ", "Liberec (updated)");
        assertTrue(getResult.getIsSuccess());
        assertEquals("Liberec (updated)", getResult.getValue().getName());
    }

    @Test
    public void testUpdateCityCountry(){
        Country targetCountry = new Country("UK", "United Kingdom");
        City city = new City("Liberec", targetCountry);

        Result<?> updateResult = cityService.updateCity("CZ", "Liberec", city);
        assertTrue(updateResult.getIsSuccess());

        Result<City> getResult = cityService.getCity("UK", "Liberec");
        assertTrue(getResult.getIsSuccess());
        assertEquals("Liberec", getResult.getValue().getName());
        assertEquals("UK", getResult.getValue().getCountry().getCode());
    }

    @Test
    public void testUpdateNonExistentCity(){
        Country targetCountry = new Country("CZ", "Czech Republic");
        City city = new City("Liberec", targetCountry);

        Result<?> updateResult = cityService.updateCity("CZ", "Liberec (updated)", city);
        assertNull(updateResult);
    }

    @Test
    public void testUpdateCityToNonExistentCountry(){
        Country targetCountry = new Country("Cze", "Czech Republic");
        City city = new City("Liberec", targetCountry);

        Result<?> updateResult = cityService.updateCity("CZ", "Liberec", city);
        assertFalse(updateResult.getIsSuccess());
        assertEquals("Cannot move city to non existent country Cze", updateResult.getError());
    }

    @Test
    public void testUpdateCityToAlreadyExistingCity(){
        Country targetCountry = new Country("CZ", "Czech Republic");
        City city = new City("Liberec", targetCountry);

        Result<?> updateResult = cityService.updateCity("CZ", "Prague", city);
        assertFalse(updateResult.getIsSuccess());
        assertEquals("City with name Liberec already exists in country CZ", updateResult.getError());
    }

    @Test
    public void testDeleteCity() {
        Result<?> deleteResult = cityService.deleteCity("CZ", "Prague");
        assertTrue(deleteResult.getIsSuccess());
    }

    @Test
    public void testDeleteNonExistentCity() {
        Result<?> deleteResult = cityService.deleteCity("CZ", "Brno");
        assertNull(deleteResult);
    }

    @Test
    public void testDeleteCityFromNonExistentCountry() {
        Result<?> deleteResult = cityService.deleteCity("Cze", "Prague");
        assertNull(deleteResult);
    }
}
