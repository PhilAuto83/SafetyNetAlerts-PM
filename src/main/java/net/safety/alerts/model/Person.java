package net.safety.alerts.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Person {

    @Pattern(regexp = "^[A-Z][a-z]{2,}$|^[A-Z][a-z]+[\\s-][A-Z][a-z]+$", message="should contain only letters, '-' or space, start with capital letter, have minimum two characters and match following examples  Li An, Li-An or Lo")
    private String firstName;
    @Pattern(regexp = "^[A-Z][a-z]+$", message="should contain only letters and start with capital letter")
    private String lastName;
    @Pattern(regexp = "^[1-9]+[a-zA-Z0-9\\s.]{4,}$", message="must start with a digit and can contain only spaces, '.', digits or letters. Length must be minimum 5 characters.")
    private String address;
    @Pattern(regexp = "^[A-Z][a-z]{2,}$|^[A-Z][a-z]{2,}[\\s][A-Z][a-z]{2,}$", message="must start with capital letter and can contain whitespace following theses examples New York or Miami")
    private String city;
    @Pattern(regexp = "[0-9]{5}", message="should have 5 digits")
    private String zip;
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$", message="number should respect format example '123-456-9999'")
    private String phone;
    @Pattern(regexp = "^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$", message="format is not valid" )
    private String email;




}
