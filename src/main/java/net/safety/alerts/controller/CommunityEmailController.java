package net.safety.alerts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotBlank;
import net.safety.alerts.service.CommunityEmailService;
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
public class CommunityEmailController {

    private static Logger logger = LoggerFactory.getLogger(CommunityEmailController.class);

    @Autowired
    private CommunityEmailService communityEmailService;
    @GetMapping(value = "/communityEmail")
    public List<String> getEmailsFromCity(@RequestParam("city") @NotBlank(message = "city name must not be null or empty.") String city) throws JsonProcessingException {
        String currentRequest = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replaceQueryParam("city", city)
                .toUriString();
        logger.info("Request launched : {}", currentRequest);
        return communityEmailService.getEmailsFromCity(city);
    }
}
