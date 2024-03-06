package net.safety.alerts.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonByFireStationDTO {

    List<PersonDTO> persons;
    int nbAdults;
    int nbChildren;


}
