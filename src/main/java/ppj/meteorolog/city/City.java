package ppj.meteorolog.city;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import ppj.meteorolog.country.Country;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "cities")
@Getter
@Setter
@NoArgsConstructor
public class City {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "City name is required")
    @Column(nullable = false)
    private String name;

    @ManyToOne
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


