package net.safety.alerts.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class MedicalRecord {

    @NotNull(message = "cannot be null")
    @Pattern(regexp = "^[A-Z][a-z]{2,}$|^[A-Z][a-z]+[\\s-][A-Z][a-z]+$",
            message="should contain only letters, '-' or space, start with capital letter, have minimum two characters and match following examples  Li An, Li-An or Lo")
    private String firstName;
    @NotNull(message = "cannot be null")
    @Pattern(regexp = "^[A-Z][a-z]+$", message="should contain only letters and start with capital letter")
    private String lastName;
    @NotNull
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @JsonAlias({"birthdate"})
    @DateTimeFormat(pattern="MM/dd/yyyy", iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
    List<String> medications;
    List<String> allergies;


}
