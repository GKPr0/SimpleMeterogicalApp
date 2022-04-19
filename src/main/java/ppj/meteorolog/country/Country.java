package ppj.meteorolog.country;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import ppj.meteorolog.city.City;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "countries")
@Data
@NoArgsConstructor
public class Country {
    @Id
    private String code;
    private String name;

    @OneToMany(mappedBy = "country")
    @JsonIgnoreProperties("country")
    private Set<City> cities;


    public Country(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
