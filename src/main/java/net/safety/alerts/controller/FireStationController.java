package net.safety.alerts.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import net.safety.alerts.dto.PersonByFireStation;
import net.safety.alerts.exceptions.PersonNotFoundException;
import net.safety.alerts.exceptions.StationNumberNotFoundException;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.service.FireStationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@Validated
public class FireStationController {

    private static final Logger logger = LoggerFactory.getLogger(FireStationController.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private FireStationService fireStationService;

    @GetMapping("/firestation")
    public PersonByFireStation getPersonInfoByStationNumber(@RequestParam(name = "stationNumber") @Pattern(regexp ="^[1-9]\\d?$",  message="number must be positive with maximum 2 digits whose minimum value starts at 1")  String stationNumber) throws JsonProcessingException {

        PersonByFireStation personByFireStation = null;
        String currentRequest = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replaceQueryParam("stationNumber", stationNumber)
                .toUriString();
        logger.info("Request launched to get person covered by a fire station through station number : {}", currentRequest);
        if(!fireStationService.doesStationNumberExist(stationNumber)) {
            logger.debug("Station number {} does not exist.", stationNumber);
            throw new StationNumberNotFoundException(String.format("Station number %s does not exist.", stationNumber));
        }
        try {
            personByFireStation = fireStationService.getPersonsInfoByStationNumber(stationNumber);
        }catch (JsonProcessingException e) {
            logger.error(e.toString());
        }
        return personByFireStation;
    }

    @PostMapping(value ="/firestation", consumes={"application/json"}, produces ={"application/json"})
    public ResponseEntity<FireStation> create(@Valid @RequestBody FireStation fireStation) throws JsonProcessingException{
        if(fireStationService.doesStationAlreadyExist(fireStation)) {
            logger.debug("Fire station with number {} and address {} already exists in file", fireStation.getStation(),
                    fireStation.getAddress());
            return ResponseEntity.noContent().build();
        }
        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();
        logger.info("Request to create a person launched : {}", currentUri);
        logger.info("Request payload : {}", mapper.writeValueAsString(fireStation));

        return ResponseEntity.created(currentUri).body(fireStationService.save(fireStation));
    }

    @DeleteMapping("firestation/{numberOrAddress}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("numberOrAddress") String numberOrAddress) throws JsonProcessingException {

        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{numberOrAddress}")
                .buildAndExpand(numberOrAddress)
                .toUri();
        logger.info("Request to delete a firestation launched : {}", currentUri);
        if(!fireStationService.doesStationNumberExist(numberOrAddress)) {
            logger.error("No station found with number or address \"{}\"", numberOrAddress);
            throw new PersonNotFoundException(String.format("No station found with number or address %s", numberOrAddress));
        }
        fireStationService.remove(numberOrAddress);
        Map<String, String> response = new HashMap<>();
        response.put("date", new Date().toString());
        response.put("message", String.format("Station with number or address %s has been deleted", numberOrAddress));
        return ResponseEntity.ok(response);
    }


}
