package net.safety.alerts.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.Pattern;
import net.safety.alerts.dto.PersonInfoDTO;
import net.safety.alerts.exceptions.PersonNotFoundException;
import net.safety.alerts.service.PersonInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
public class PersonInfoController {

    private static final Logger logger = LoggerFactory.getLogger(PersonInfoController.class);

    @Autowired
    private PersonInfoService personInfoService;

    @GetMapping("/personInfo")
    public List<PersonInfoDTO> getPersonInfoByStationNumber(@RequestParam(value = "firstName", required = false) String firstName, @RequestParam(value = "lastName") @Pattern(regexp = "[a-zA-Z]{2,}", message= "lastname must be at least 2 characters long with letters only") String lastName) throws JsonProcessingException {

        logger.info("Request launched to get a list of person by name {} with optional firstname {} grouped by station address",lastName, firstName);
        List<PersonInfoDTO> personInfoDTOList = new ArrayList<>();
        if(!personInfoService.doesPersonExists(firstName, lastName)){
            logger.error("Person with firstname \"{}\" and lastname \"{}\" was not found.", firstName, lastName);
            throw new PersonNotFoundException(String.format("Person with firstname '%s' and lastname '%s' was not found.", firstName, lastName));
        }
        return personInfoService.getPersonList(firstName, lastName);
    }
}


