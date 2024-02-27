package net.safety.alerts.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChildAlertDTO {

    List<PersonAgeDTO> childrenList;
    List<PersonAgeDTO> otherMembers;
}
