package ppj.meteorolog.country;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ppj.meteorolog.shared.Result;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest //TODO Zjistit ja tady použít JPADataTest anotaci
@ActiveProfiles("test")
public class CountryServiceTest {

    @Autowired
    private CountryDataInitializer dataInitializer;

    @Autowired
    private CountryService countryService;

    @BeforeEach
    public void setup() {
        dataInitializer.setup();
    }

    @AfterEach
    public void clear() {
        dataInitializer.clear();
    }

    @Test
    public void testGetAllCountries() {
        Result<Iterable<Country>> getResult = countryService.getAllCountries();

        assertTrue(getResult.getIsSuccess());
        assertEquals(getResult.getValue().spliterator().getExactSizeIfKnown(),4);
    }

    @Test
    public void testGetCountryByCode() {
        Result<Country> getResult = countryService.getCountry("CZ");

        assertTrue(getResult.getIsSuccess());
        assertEquals(getResult.getValue().getName(),"Czech Republic");
        assertEquals(getResult.getValue().getCode(),"CZ");
    }

    @Test
    public void testGetCountryWithNonExistingCode() {
        Result<Country> getResult = countryService.getCountry("CZe");

        assertNull(getResult);
    }

    @Test
    public void testCreateCountry() {
        Country countryToCreate = new Country("DE", "Germany");

        Result<?> createResult = countryService.createCountry(countryToCreate);
        assertTrue(createResult.getIsSuccess());

        Result<Country> getResult = countryService.getCountry("DE");
        assertTrue(getResult.getIsSuccess());
        assertEquals(getResult.getValue().getName(),"Germany");
        assertEquals(getResult.getValue().getCode(),"DE");
    }

    @Test
    public void testCreateCountryWithAlreadyExistingCode() {
        Country countryToCreate = new Country("CZ", "Czech Republic");

        Result<?> createResult = countryService.createCountry(countryToCreate);
        assertFalse(createResult.getIsSuccess());
        assertEquals(createResult.getError(),"Country with code CZ already exists");
    }

    @Test
    public void testUpdateCountry() {
        Country updatedCountry = new Country("CZ", "Czech Republic (updated)");

        Result<?> updateResult = countryService.updateCountry("CZ", updatedCountry);
        assertTrue(updateResult.getIsSuccess());

        Result<Country> getResult = countryService.getCountry("CZ");
        assertTrue(getResult.getIsSuccess());
        assertEquals(getResult.getValue().getName(),"Czech Republic (updated)");
        assertEquals(getResult.getValue().getCode(),"CZ");
    }

    @Test
    public void testUpdateCountryWithNonExistingCode() {
        Country updatedCountry = new Country("CZ", "Czech Republic (updated)");

        Result<?> updateResult = countryService.updateCountry("CZe", updatedCountry);
        assertNull(updateResult);
    }

    @Test
    public void testDeleteCountry() {
        Result<?> deleteResult = countryService.deleteCountry("CZ");
        assertTrue(deleteResult.getIsSuccess());

        Result<Country> getResult = countryService.getCountry("CZ");
        assertNull(getResult);
    }

    @Test
    public void testDeleteCountryWithNonExistingCode() {
        Result<?> deleteResult = countryService.deleteCountry("CZe");
        assertNull(deleteResult);
    }
}