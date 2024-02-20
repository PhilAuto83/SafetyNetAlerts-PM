package net.safety.alerts.model;


import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$")
    private String phone;
    @Pattern(regexp = "^[a-z]+@[a-z]{2,}.[a-z]{2,}" )
    private String email;




}
