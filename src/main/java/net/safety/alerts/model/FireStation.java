package net.safety.alerts.model;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class FireStation {

    @Pattern(regexp = "^[1-9]+[a-zA-Z0-9\\s.]{4,}$",
            message="must start with a digit and can contain only spaces, '.', digits or letters. Length must be minimum 5 characters.")
    private String address;
    @Pattern(regexp = "^[1-9]\\d?$", message="station number must be positive with maximum 2 digits whose minimum value starts at 1")
    private String station;

}
