package net.safety.alerts.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertsData {

    private List<Person>persons;

    @JsonAlias({"firestations"})
    private List<FireStation> fireStations;

    @JsonAlias({"medicalrecords"})
    private List<MedicalRecord>medicalRecords;


}
