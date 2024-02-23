package net.safety.alerts.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonInfoDTO {

    private String fullName;
    private String address;
    private int age;
    private String email;
    private Map<String, List<String>> medicalInfos;
}
