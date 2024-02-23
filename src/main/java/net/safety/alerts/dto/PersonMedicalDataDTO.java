package net.safety.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonMedicalDataDTO {

    private String fullName;
    private String phone;
    private int age;
    private Map<String, List<String>> medicalData;

}
