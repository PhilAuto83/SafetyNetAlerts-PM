package net.safety.alerts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import net.safety.alerts.exceptions.PersonNotFoundException;
import net.safety.alerts.model.Person;
import net.safety.alerts.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PersonController {

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private PersonService personService;

    @PostMapping(value = "/person", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) throws JsonProcessingException {

        if(personService.doesPersonAlreadyExist(person)){
            logger.debug("Person with firstname {} and lastname {} already exists in file", person.getFirstName(), person.getLastName());
            return ResponseEntity.noContent().build();
        }
        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(person)
                .toUri();
        logger.info("Request to create a person launched : {}", currentUri);

        return ResponseEntity.created(currentUri).body(personService.save(person));
    }

    @PutMapping(value = "/person", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<Person> update(@Valid @RequestBody Person person) throws JsonProcessingException {

        if(!personService.doesPersonAlreadyExist(person)){
            logger.error("No person found with firstname {} and lastname {}", person.getFirstName(), person.getLastName());
            throw new PersonNotFoundException(String.format("No person found with firstname %s and lastname %s", person.getFirstName(), person.getLastName()));
        }
        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(person)
                .toUri();
        logger.info("Request to update a person launched : {}", currentUri);

        return ResponseEntity.ok(personService.update(person));
    }

    @DeleteMapping(value ="/person", consumes={"application/json"}, produces = "application/json")
    public ResponseEntity<Map<String, String>> delete(@Valid @RequestBody Person person) throws JsonProcessingException {
        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(person)
                .toUri();
        logger.info("Request to delete a person launched : {}", currentUri);
        if(!personService.doesPersonAlreadyExist(person)){
            logger.error("No person found with firstname {} and lastname {}", person.getFirstName(), person.getLastName());
            throw new PersonNotFoundException(String.format("No person found with firstname %s and lastname %s", person.getFirstName(), person.getLastName()));
        }
        personService.remove(person.getFirstName(), person.getLastName());
        Map<String, String> params = new HashMap<>();
        params.put("date", new Date().toString());
        params.put("message", String.format("Person with firstname %s and lastname %s has been deleted", person.getFirstName(), person.getLastName()));
        params.put("status code", HttpStatus.OK.name());
        return ResponseEntity.ok(params);
    }
}
