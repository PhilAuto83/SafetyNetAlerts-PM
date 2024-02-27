package net.safety.alerts.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FloodDTO {

    private String address;
    List<PersonMedicalDataDTO> persons;
}
