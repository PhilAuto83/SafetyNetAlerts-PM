package net.safety.alerts.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dto.PersonInfoDTO;
import net.safety.alerts.service.PersonInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
public class PersonInfoController {

    private static final Logger logger = LoggerFactory.getLogger(PersonInfoController.class);

    @Autowired
    private PersonInfoService personInfoService;

    @GetMapping("/personInfo")
    public List<PersonInfoDTO> getPersonInfoByStationNumber(@RequestParam(value = "firstName", required = false) String firstName, @RequestParam(value = "lastName") String lastName) throws JsonProcessingException {

        List<PersonInfoDTO> personInfoDTOList = new ArrayList<>();
        String currentRequest = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replaceQueryParam("firstName", firstName)
                .replaceQueryParam("lastName", lastName)
                .toUriString();
        logger.info("Request launched : {}", currentRequest);

        return personInfoDTOList;
    }
}


