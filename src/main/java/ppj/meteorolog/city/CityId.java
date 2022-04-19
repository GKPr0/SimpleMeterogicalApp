package ppj.meteorolog.city;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ppj.meteorolog.country.Country;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CityId implements Serializable {
    private String name;
    private Country country;
}
