package net.safety.alerts.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.Pattern;
import net.safety.alerts.dto.FloodDTO;
import net.safety.alerts.exceptions.StationNumberNotFoundException;
import net.safety.alerts.service.FireStationService;
import net.safety.alerts.service.FloodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@Validated
public class FloodController {

    private static final Logger logger = LoggerFactory.getLogger(FloodController.class);

    @Autowired
    private FloodService floodService;
    @Autowired
    private FireStationService fireStationService;

    @GetMapping("/flood/stations")
    public List<FloodDTO> getPersonMedicalDataByStationNumber(@RequestParam("stations") @Pattern(regexp ="^[1-9]\\d?$",  message="station number must be positive with maximum 2 digits whose minimum value starts at 1")
                                                                  String stationNumber) throws JsonProcessingException {
        String currentRequest = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replaceQueryParam("stations", stationNumber)
                .toUriString();
        logger.info("Request launched to get persons medical data by stationNumber for flood incident : {}", currentRequest);
        if(!fireStationService.doesStationNumberExist(stationNumber)) {
            logger.debug("Station number {} does not exist.", stationNumber);
            throw new StationNumberNotFoundException(String.format("Station number %s does not exist.", stationNumber));
        }
        return floodService.getPersonsWithMedicalDataFromStationNumber(stationNumber);
    }
}
