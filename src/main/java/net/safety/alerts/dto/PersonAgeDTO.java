package net.safety.alerts.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonAgeDTO {

    private String firstName;
    private String lastName;
    private int age;
}
