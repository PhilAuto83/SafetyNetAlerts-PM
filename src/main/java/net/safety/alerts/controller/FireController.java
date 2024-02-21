package net.safety.alerts.controller;

import jakarta.validation.constraints.NotBlank;
import net.safety.alerts.dto.FireDTO;
import net.safety.alerts.service.FireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class FireController {

    @Autowired
    private FireService fireService;

    @GetMapping("/fire")
    public FireDTO getPersonMedicalDataByAddress(@RequestParam("address") @NotBlank String address){
        return fireService.getPersonMedicalInfoByAddress(address);

    }
}
