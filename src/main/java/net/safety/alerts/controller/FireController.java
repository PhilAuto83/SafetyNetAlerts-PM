package net.safety.alerts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotBlank;
import net.safety.alerts.dto.FireDTO;
import net.safety.alerts.service.FireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@Validated
public class FireController {

    private static final Logger logger = LoggerFactory.getLogger(FireController.class);

    @Autowired
    private FireService fireService;

    @GetMapping("/fire")
    public FireDTO getPersonMedicalDataByAddress(@RequestParam("address") @NotBlank(message= "address cannot be null or empty") String address) throws JsonProcessingException {
        String currentRequest = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replaceQueryParam("address", address)
                .toUriString();
        logger.info("Request launched to get persons medical data  by address for fire incident : {}", currentRequest);
        return fireService.getPersonMedicalInfoByAddress(address);

    }
}
