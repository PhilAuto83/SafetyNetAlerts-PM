package net.safety.alerts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.Pattern;
import net.safety.alerts.exceptions.StationNumberNotFoundException;
import net.safety.alerts.service.FireStationService;
import net.safety.alerts.service.PhoneAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@Validated
public class PhoneAlertController {

    private static final Logger logger = LoggerFactory.getLogger(PhoneAlertController.class);

    @Autowired
    private PhoneAlertService phoneAlertService;

    @Autowired
    private FireStationService fireStationService;

    @GetMapping("/phoneAlert")
    public Set<String> getPhonesByFireStation(@RequestParam("firestation") @Pattern(regexp ="^[1-9]\\d?$",
            message="station number must be positive with maximum 2 digits whose minimum value starts at 1") String stationNumber) throws JsonProcessingException {
        Set<String> phones = new HashSet<>();

        if(!fireStationService.doesStationNumberExist(stationNumber)) {
            logger.debug("Station number {} does not exist.", stationNumber);
            throw new StationNumberNotFoundException(String.format("Station number %s does not exist.", stationNumber));
        }
        return phoneAlertService.getPhonesByFireStation(stationNumber);
    }
}
