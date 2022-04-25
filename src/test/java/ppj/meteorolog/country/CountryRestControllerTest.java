package ppj.meteorolog.country;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
public class CountryRestControllerTest {

    @Autowired
    private CountryDataInitializer dataInitializer;

    @Autowired
    private CountryRepository countryRepository;

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
    public void testGetCountries_thenStatus200() throws Exception {
        mvc.perform(get("/api/v1/country"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].name", is("Czech Republic")))
                .andExpect(jsonPath("$[0].code", is("CZ")))
                .andExpect(jsonPath("$[1].name", is("United Kingdom")))
                .andExpect(jsonPath("$[1].code", is("UK")));
    }

    @Test
    public void testGetCountry_thenStatus200() throws Exception {
        mvc.perform(get("/api/v1/country/CZ"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Czech Republic")))
                .andExpect(jsonPath("$.code", is("CZ")));
    }

    @Test
    public void testGetNonExistentCountry_thenStatus404() throws Exception {
        mvc.perform(get("/api/v1/country/XX"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCountry_thenStatus200() throws Exception {
        mvc.perform(post("/api/v1/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"code\":\"TT\"}"))
                .andExpect(status().isOk());

        Optional<Country> createdCountry = countryRepository.findByCode("TT");
        assertTrue(createdCountry.isPresent());
        assertEquals("Test", createdCountry.get().getName());
        assertEquals("TT", createdCountry.get().getCode());
    }

    @Test
    public void testCreateCountryWithAlreadyExistingCode_thenStatus400() throws Exception {
        mvc.perform(post("/api/v1/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"code\":\"CZ\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateCountryWithMissingCode_thenStatus400() throws Exception {
        mvc.perform(post("/api/v1/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("Country code is required")));
    }

    @Test
    public void testCreateCountryWithEmptyCode_thenStatus400() throws Exception {
        mvc.perform(post("/api/v1/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"code\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("Country code is required")));
    }

    @Test
    public void testCreateCountryWithNullCode_thenStatus400() throws Exception {
        mvc.perform(post("/api/v1/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test\",\"code\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("Country code is required")));
    }

    @Test
    public void testCreateCountryWithMissingName_thenStatus400() throws Exception {
        mvc.perform(post("/api/v1/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"TT\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("Country name is required")));
    }

    @Test
    public void testCreateCountryWithEmptyName_thenStatus400() throws Exception {
        mvc.perform(post("/api/v1/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"code\":\"TT\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("Country name is required")));
    }

    @Test
    public void testCreateCountryWithNullName_thenStatus400() throws Exception {
        mvc.perform(post("/api/v1/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":null,\"code\":\"TT\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("Country name is required")));
    }

    @Test
    public void testCreateCountryWithEmptyJson_thenStatus400() throws Exception {
        mvc.perform(post("/api/v1/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateCountry_thenStatus200() throws Exception {
        mvc.perform(put("/api/v1/country/CZ")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Czech Republic (Updated)\",\"code\":\"CZE\"}"))
                .andExpect(status().isOk());

        Optional<Country> updatedCountry = countryRepository.findByCode("CZE");
        assertTrue(updatedCountry.isPresent());
        assertEquals(updatedCountry.get().getName(), "Czech Republic (Updated)");
    }

    @Test
    public void testUpdateNonExistentCountry_thenStatus404() throws Exception {
        mvc.perform(put("/api/v1/country/XX")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"code\":\"TT\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateCountryWithMissingCode_thenStatus400() throws Exception {
        mvc.perform(put("/api/v1/country/CZ")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("Country code is required")));
    }

    @Test
    public void testUpdateCountryWithEmptyCode_thenStatus400() throws Exception {
        mvc.perform(put("/api/v1/country/CZ")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"code\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("Country code is required")));
    }

    @Test
    public void testUpdateCountryWithNullCode_thenStatus400() throws Exception {
        mvc.perform(put("/api/v1/country/CZ")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"code\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("Country code is required")));
    }

    @Test
    public void testUpdateCountryWithMissingName_thenStatus400() throws Exception {
        mvc.perform(put("/api/v1/country/CZ")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"TT\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("Country name is required")));
    }

    @Test
    public void testUpdateCountryWithEmptyName_thenStatus400() throws Exception {
        mvc.perform(put("/api/v1/country/CZ")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"code\":\"TT\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("Country name is required")));
    }

    @Test
    public void testUpdateCountryWithNullName_thenStatus400() throws Exception {
        mvc.perform(put("/api/v1/country/CZ")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":null,\"code\":\"TT\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", is("Country name is required")));
    }

    @Test
    public void testUpdateCountryWithEmptyJson_thenStatus400() throws Exception {
        mvc.perform(put("/api/v1/country/CZ")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteCountry_thenStatus200() throws Exception {
        mvc.perform(delete("/api/v1/country/CZ"))
                .andExpect(status().isOk());

        Optional<Country> country = countryRepository.findByCode("CZ");
        assertTrue(country.isEmpty());
    }

    @Test
    public void testDeleteNonExistentCountry_thenStatus404() throws Exception {
        mvc.perform(delete("/api/v1/country/XX"))
                .andExpect(status().isNotFound());
    }
}
