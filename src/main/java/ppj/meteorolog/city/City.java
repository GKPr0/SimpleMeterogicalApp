package ppj.meteorolog.city;


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

    @NotBlank(message = "Display name is required")
    private String displayName;

    @Id
    @ManyToOne
    @PrimaryKeyJoinColumn
    @NotNull(message = "Country is required")
    private Country country;

    private double latitude;

    private double longitude;
}


