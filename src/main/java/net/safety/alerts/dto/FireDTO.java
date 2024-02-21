package net.safety.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FireDTO {

    private List<PersonMedicalDataDTO> persons;
    private String stationNumber;

}
