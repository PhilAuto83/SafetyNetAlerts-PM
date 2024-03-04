package net.safety.alerts.dto;


import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonInfoDTO {

    private String fullName;
    private String address;
    private int age;
    private String email;
    private Map<String, List<String>> medicalInfos;
}
