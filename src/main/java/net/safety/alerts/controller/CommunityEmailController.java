package net.safety.alerts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotBlank;
import net.safety.alerts.service.CommunityEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class CommunityEmailController {

    @Autowired
    private CommunityEmailService communityEmailService;
    @GetMapping(value = "/communityEmail")
    public List<String> getEmailsFromCity(@RequestParam("city") @NotBlank(message = "city name must not be null or empty.") String city) throws JsonProcessingException {
        return communityEmailService.getEmailsFromCity(city);
    }
}
