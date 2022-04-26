package ppj.meteorolog.city;

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

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CityRestControllerTest {

    @Autowired
    private CityDataInitializer dataInitializer;

    @Autowired
    private CityRepository cityRepository;

    @BeforeEach
    public void setup() {
        dataInitializer.setup();
    }

    @AfterEach
    public void clean() {
        dataInitializer.clear();
    }

    @Autowired
    private MockMvc mvc;

    @Test
    public void testGetAllCities_thenStatus200() throws Exception{
        mvc.perform(get("/api/v1/city"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(9)))
                .andExpect(jsonPath("$[0].name", is("Liberec")))
                .andExpect(jsonPath("$[3].name", is("Cambridge")))
                .andExpect(jsonPath("$[8].name", is("Milan")));
    }

    @Test
    public void testGetAllCitiesFromCountry_thenStatus200() throws Exception{
        mvc.perform(get("/api/v1/city/CZ"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Liberec")))
                .andExpect(jsonPath("$[1].name", is("Prague")));
    }

    @Test
    public void testGetAllCitiesFromInvalidCountry_thenStatus404() throws Exception{
        mvc.perform(get("/api/v1/city/XX"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCity_thenStatus200() throws Exception{
        mvc.perform(get("/api/v1/city/CZ/Liberec"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Liberec")))
                .andExpect(jsonPath("$.country.name", is("Czech Republic")));
    }

    @Test
    public void testGetNonExistentCity_thenStatus404() throws Exception{
        mvc.perform(get("/api/v1/city/CZ/NewYork"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetCityFromInvalidCountry_thenStatus404() throws Exception{
        mvc.perform(get("/api/v1/city/XX/Liberec"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCity_thenStatus200() throws Exception{
        mvc.perform(post("/api/v1/city")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Brno\",\"country\":{\"code\":\"CZ\"}}"))
                .andExpect(status().isOk());

        Optional<City> createdCity = cityRepository.findCityByNameAndCountry_Code("Brno", "CZ");
        assertTrue(createdCity.isPresent());
        assertEquals("Brno", createdCity.get().getName());
        assertEquals("CZ", createdCity.get().getCountry().getCode());
    }

    @Test
    public void testCreateCityInNonExistentCountry_thenStatus400() throws Exception{
        mvc.perform(post("/api/v1/city")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Brno\",\"country\":{\"code\":\"XX\"}}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateAlreadyExistingCityInSpecificCountry_thenStatus400() throws Exception{
        mvc.perform(post("/api/v1/city")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Prague\",\"country\":{\"code\":\"CZ\"}}"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"country\":{\"code\":\"CZ\"}}",
            "{\"name\":\"\",\"country\":{\"code\":\"CZ\"}}",
            "{\"name\":null,\"country\":{\"code\":\"CZ\"}}"
            })
    public void testCreateCityWithInvalidName_thenStatus400(String contentBody) throws Exception{
        mvc.perform(post("/api/v1/city")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("City name is required")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"Brno\"}",
            "{\"name\":\"Brno\",\"country\":null}"
            })
    public void testCreateCityWithInvalidCountry_thenStatus400(String contentBody) throws Exception{
        mvc.perform(post("/api/v1/city")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.country", is("Country is required")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"Brno\",\"country\":\"\"}",
            "{\"name\":\"Brno\",\"country\":{}}",
            "{\"name\":\"Brno\",\"country\":{\"code\":null}}",
            "{\"name\":\"Brno\",\"country\":{\"code\":\"\"}}",
            "{\"name\":\"Brno\",\"country\":{\"nocode\":\"CZ\"}}"
    })
    public void testCreateCityWithInvalidCountryContent_thenStatus400(String contentBody) throws Exception{
        mvc.perform(post("/api/v1/city")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateCityWithEmptyBody_thenStatus400() throws Exception{
        mvc.perform(post("/api/v1/city")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateCity_thenStatus200() throws Exception{
        mvc.perform(put("/api/v1/city/CZ/Liberec" )
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Liberec (updated)\",\"country\":{\"code\":\"IT\"}}"))
                .andExpect(status().isOk());

        Optional<City> updatedCity = cityRepository.findCityByNameAndCountry_Code("Liberec (updated)", "IT");
        assertTrue(updatedCity.isPresent());
        assertEquals("Liberec (updated)", updatedCity.get().getName());
        assertEquals("IT", updatedCity.get().getCountry().getCode());
    }

    @Test
    public void testUpdateNonExistentCity_thenStatus404() throws Exception{
        mvc.perform(put("/api/v1/city/CZ/Brno" )
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Brno (updated)\",\"country\":{\"code\":\"CZ\"}}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateCityInNonExistentCountry_thenStatus404() throws Exception{
        mvc.perform(put("/api/v1/city/IT/Brno" )
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Brno (updated)\",\"country\":{\"code\":\"IT\"}}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateCityToNonExistentCountry_thenStatus400() throws Exception{
        mvc.perform(put("/api/v1/city/CZ/Liberec" )
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Liberec (updated)\",\"country\":{\"code\":\"CZE\"}}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateCityToAlreadyExistingCityInSpecificCountry_thenStatus400() throws Exception{
        mvc.perform(put("/api/v1/city/CZ/Prague" )
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Liberec\",\"country\":{\"code\":\"CZ\"}}"))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"country\":{\"code\":\"CZ\"}}",
            "{\"name\":\"\",\"country\":{\"code\":\"CZ\"}}",
            "{\"name\":null,\"country\":{\"code\":\"CZ\"}}"
    })
    public void testUpdateCityWithInvalidName_thenStatus400(String contentBody) throws Exception{
        mvc.perform(put("/api/v1/city/CZ/Liberec" )
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("City name is required")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"Brno\"}",
            "{\"name\":\"Brno\",\"country\":null}"
    })
    public void testUpdateCityWithInvalidCounty_thenStatus400(String contentBody) throws Exception{
        mvc.perform(put("/api/v1/city/CZ/Liberec")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.country", is("Country is required")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"name\":\"Brno\",\"country\":\"\"}",
            "{\"name\":\"Brno\",\"country\":{}}",
            "{\"name\":\"Brno\",\"country\":{\"code\":null}}",
            "{\"name\":\"Brno\",\"country\":{\"code\":\"\"}}",
            "{\"name\":\"Brno\",\"country\":{\"nocode\":\"CZ\"}}"
    })
    public void testUpdateCityWithInvalidCountryContent_thenStatus400(String contentBody) throws Exception{
        mvc.perform(put("/api/v1/city/CZ/Liberec" )
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateCityWithEmptyBody_thenStatus400() throws Exception{
        mvc.perform(put("/api/v1/city/CZ/Liberec" )
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteCity_thenStatus200() throws Exception{
        mvc.perform(delete("/api/v1/city/CZ/Liberec"))
                .andExpect(status().isOk());

        Optional<City> deletedCity = cityRepository.findCityByNameAndCountry_Code("Liberec", "CZ");
        assertTrue(deletedCity.isEmpty());
    }

    @Test
    public void testDeleteNonExistentCity_thenStatus404() throws Exception{
        mvc.perform(delete("/api/v1/city/CZ/XX"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCityInNonExistentCountry_thenStatus404() throws Exception{
        mvc.perform(delete("/api/v1/city/XX/Liberec"))
                .andExpect(status().isNotFound());
    }
}
