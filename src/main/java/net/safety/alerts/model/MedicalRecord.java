package net.safety.alerts.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecord {
    private String firstName;
    private String lastName;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @JsonAlias({"birthdate"})

    private LocalDate birthDate;
    List<String> medications;
    List<String> allergies;


}
