package net.safety.alerts.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public class MedicalRecord {

    private String firstName;
    private String lastName;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")

    private LocalDate birthdate;
    List<String> medications;
    List<String> allergies;
}
