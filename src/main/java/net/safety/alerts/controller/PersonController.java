package net.safety.alerts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import net.safety.alerts.model.Person;
import net.safety.alerts.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class PersonController {

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private PersonService personService;

    @PostMapping(value = "/person", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) throws JsonProcessingException {

        if(personService.doesPersonAlreadyExist(person)){
            return ResponseEntity.noContent().build();
        }
        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(person)
                .toUri();
        logger.info("Request launched : {}", currentUri);
        personService.save(person);
        return ResponseEntity.created(currentUri).build();
    }
}
