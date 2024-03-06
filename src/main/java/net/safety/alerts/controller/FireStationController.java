package net.safety.alerts.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import net.safety.alerts.dto.PersonByFireStationDTO;
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
    public PersonByFireStationDTO getPersonInfoByStationNumber(@RequestParam(name = "stationNumber") @Pattern(regexp ="^[1-9]\\d?$",  message="number must be positive with maximum 2 digits whose minimum value starts at 1")  String stationNumber) throws JsonProcessingException {

        PersonByFireStationDTO personByFireStationDTO = null;
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
            personByFireStationDTO = fireStationService.getPersonsInfoByStationNumber(stationNumber);
        }catch (JsonProcessingException e) {
            logger.error(e.toString());
        }
        return personByFireStationDTO;
    }

    @PostMapping(value ="/firestation", consumes={"application/json"}, produces ={"application/json"})
    public ResponseEntity<FireStation> create(@Valid @RequestBody FireStation fireStation) throws JsonProcessingException{
        if(fireStationService.doesStationAlreadyExist(fireStation.getAddress(), fireStation.getStation())) {
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

    @PutMapping(value="/firestation", consumes={"application/json"}, produces={"application/json"})
    public ResponseEntity<FireStation> update(@Valid @RequestBody FireStation newStation) throws JsonProcessingException {
        if(!fireStationService.doesNumberOrAddressExists(newStation.getAddress())){
            logger.error("No station found with address \"{}\"", newStation.getAddress());
            throw new StationNumberNotFoundException(String.format("No station found with address %s", newStation.getAddress()));
        }else if(fireStationService.doesStationAlreadyExist(newStation.getAddress(), newStation.getStation())){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fireStationService.update(newStation));
    }

    @DeleteMapping("firestation/{numberOrAddress}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("numberOrAddress") String numberOrAddress) throws JsonProcessingException {

        if(!fireStationService.doesNumberOrAddressExists(numberOrAddress)) {
            logger.error("No station found with number or address \"{}\"", numberOrAddress);
            throw new StationNumberNotFoundException(String.format("No station found with number or address %s", numberOrAddress));
        }
        fireStationService.remove(numberOrAddress);
        Map<String, String> response = new HashMap<>();
        response.put("date", new Date().toString());
        response.put("message", String.format("List of stations with number or address %s have been removed successfully", numberOrAddress));
        return ResponseEntity.ok(response);
    }
}
