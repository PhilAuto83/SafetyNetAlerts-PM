package net.safety.alerts.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import net.safety.alerts.dto.PersonByFireStation;
import net.safety.alerts.service.FireStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<PersonByFireStation> PersonsInfoByStationNumber(@RequestParam(value = "stationNumber") @Min(value = 1,  message="Station number must be an integer whose minimum value must be 1") int stationNumber){
        PersonByFireStation personByFireStation = null;
        logger.info("Request launched : /firestation?stationNumber= "+stationNumber);
        if(!fireStationService.doesStationNumberExist(stationNumber)){
            logger.debug("Station number {} does not exist.", stationNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            personByFireStation = fireStationService.getPersonsInfoByStationNumber(stationNumber);
        }catch (JsonProcessingException e) {
            logger.error(e.toString());
        }
        return ResponseEntity.ofNullable(personByFireStation);
    }
}
