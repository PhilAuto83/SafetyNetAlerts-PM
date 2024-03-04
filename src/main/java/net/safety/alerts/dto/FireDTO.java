package net.safety.alerts.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FireDTO {

    private String stationNumber;
    private List<PersonMedicalDataDTO> persons;

}
