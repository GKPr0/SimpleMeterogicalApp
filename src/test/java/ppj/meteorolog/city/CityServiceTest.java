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
        assertEquals(getResult.getValue().spliterator().getExactSizeIfKnown(),9);
    }

    @Test
    public void testGetAllCitiesInCountry() {
        Result<Iterable<City>> getResult = cityService.getAllCitiesInCountry("CZ");

        assertTrue(getResult.getIsSuccess());
        assertEquals(getResult.getValue().spliterator().getExactSizeIfKnown(),2);
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
        assertEquals(getResult.getValue().getName(),"Prague");
        assertEquals(getResult.getValue().getCountry().getCode(),"CZ");
    }

    @Test
    public void testGetCityFromNonExistentCountry() {
        Result<City> getResult = cityService.getCity("Cze", "Prague");

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
        assertEquals(getResult.getValue().getName(),"Brno");
        assertEquals(getResult.getValue().getCountry().getCode(),"CZ");
    }

    @Test
    public void testCreateAlreadyExistingCity() {
        Country targetCountry = new Country("CZ", "Czech Republic");
        City city = new City("Prague", targetCountry);

        Result<?> createResult = cityService.createCity(city);
        assertFalse(createResult.getIsSuccess());
        assertEquals(createResult.getError(),"City with name Prague already exists in country CZ");
    }

    @Test
    public void testCreateCityInNonExistentCountry() {
        Country targetCountry = new Country("Cze", "Czech Republic");
        City city = new City("Brno", targetCountry);

        Result<?> createResult = cityService.createCity(city);
        assertFalse(createResult.getIsSuccess());
        assertEquals(createResult.getError(),"Cannot add city to non existent country Cze");
    }

    @Test
    public void testUpdateCityName() {
        Country targetCountry = new Country("CZ", "Czech Republic");
        City city = new City("Liberec (updated)", targetCountry);

        Result<?> updateResult = cityService.updateCity("CZ", "Liberec", city);
        assertTrue(updateResult.getIsSuccess());

        Result<City> getResult = cityService.getCity("CZ", "Liberec (updated)");
        assertTrue(getResult.getIsSuccess());
        assertEquals(getResult.getValue().getName(),"Liberec (updated)");
    }

    @Test
    public void testUpdateCityCountry(){
        Country targetCountry = new Country("UK", "United Kingdom");
        City city = new City("Liberec", targetCountry);

        Result<?> updateResult = cityService.updateCity("CZ", "Liberec", city);
        assertTrue(updateResult.getIsSuccess());

        Result<City> getResult = cityService.getCity("UK", "Liberec");
        assertTrue(getResult.getIsSuccess());
        assertEquals(getResult.getValue().getName(),"Liberec");
        assertEquals(getResult.getValue().getCountry().getCode(),"UK");
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
        assertEquals(updateResult.getError(),"Cannot move city to non existent country Cze");
    }

    @Test
    public void testUpdateCityToAlreadyExistingCity(){
        Country targetCountry = new Country("CZ", "Czech Republic");
        City city = new City("Liberec", targetCountry);

        Result<?> updateResult = cityService.updateCity("CZ", "Prague", city);
        assertFalse(updateResult.getIsSuccess());
        assertEquals(updateResult.getError(),"City with name Liberec already exists in country CZ");
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
