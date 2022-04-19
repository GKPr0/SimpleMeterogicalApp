package ppj.meteorolog.city;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ppj.meteorolog.country.Country;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "cities")
@Data
@NoArgsConstructor
@IdClass(CityId.class)
public class City {

    @Id
    @NotBlank(message = "City name is required")
    private String name;

    @Id
    @ManyToOne
    @JoinColumn(name = "country_code", referencedColumnName="code")
    @NotNull(message = "Country is required")
    @JsonIgnoreProperties("cities")
    private Country country;

    private double latitude;

    private double longitude;

    public City(String name, Country country) {
        this.name = name;
        this.country = country;
    }
}


