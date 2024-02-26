package net.safety.alerts.controller;

import jakarta.validation.constraints.Pattern;
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
    public List<String> getEmailsFromCity(@RequestParam("city") @Pattern(regexp = "^[a-z][A-Z]{2,}$", message = "city name must have at least 2 letters.") String city){
        return communityEmailService.getEmailsFromCity(city);
    }
}