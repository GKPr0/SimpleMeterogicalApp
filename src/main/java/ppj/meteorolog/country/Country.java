package ppj.meteorolog.country;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ppj.meteorolog.city.City;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class Country {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Country code is required")
    @Column(unique = true, nullable = false)
    private String code;

    @NotBlank(message = "Country name is required")
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("country")
    private Set<City> cities;

    public Country(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
