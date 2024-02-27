package net.safety.alerts.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PersonMedicalDataDTO {

    private String fullName;
    private String phone;
    private int age;
    private Map<String, List<String>> medicalInfos;

}
