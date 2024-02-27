package net.safety.alerts.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Person {

    @Pattern(regexp = "^[a-zA-Z]{2,}[\\s-]?[a-zA-Z]*$", message="must start with 2 letters and can contain whitespace or '-'")
    private String firstName;
    @Pattern(regexp = "[a-zA-Z]{2,}", message="should contains only letters and minimum 2")
    private String lastName;
    @Pattern(regexp = "^[1-9]+[a-zA-Z0-9\\s.]{4,}$", message="must start with a digit and can contain only spaces, '.', digits or letters")
    private String address;
    @Pattern(regexp = "^[a-zA-Z]{2,}[\\s-]?[a-zA-Z]*$", message="must start with 2 letters and can contain whitespace or '-'")
    private String city;
    @Pattern(regexp = "[0-9]{5}", message="should have 5 digits")
    private String zip;
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$", message="number should respect format example '123-456-9999'")
    private String phone;
    @Pattern(regexp = "^[a-z]+@[a-z]{2,}.[a-z]{2,}", message="format is not valid, minimum format should be 'a@bc.fr'" )
    private String email;




}
