package net.safety.alerts.controller;


import com.fasterxml.jackson.core.JsonProcessingException;


import jakarta.validation.constraints.Pattern;
import net.safety.alerts.dto.PersonByFireStation;
import net.safety.alerts.exceptions.StationNumberNotFoundException;
import net.safety.alerts.service.FireStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@Validated
public class FireStationController {

    private static final Logger logger = LoggerFactory.getLogger(FireStationController.class);

    @Autowired
    private FireStationService fireStationService;

    @GetMapping("/firestation")
    public ResponseEntity<PersonByFireStation> getPersonInfoByStationNumber(@RequestParam(name = "stationNumber") @Pattern(regexp ="^[1-9]\\d?$",  message="Station number must be a positive number with maximum 2 digits whose minimum value starts at 1")  String stationNumber){

        PersonByFireStation personByFireStation = null;
        logger.info("Request launched : /firestation?stationNumber= "+stationNumber);
        if(!fireStationService.doesStationNumberExist(stationNumber)) {
            logger.debug("Station number {} does not exist.", stationNumber);
            throw new StationNumberNotFoundException(String.format("Station number %s does not exist.", stationNumber));
        }
        try {
            personByFireStation = fireStationService.getPersonsInfoByStationNumber(stationNumber);
        }catch (JsonProcessingException e) {
            logger.error(e.toString());
        }
        return ResponseEntity.ofNullable(personByFireStation);
    }
}
