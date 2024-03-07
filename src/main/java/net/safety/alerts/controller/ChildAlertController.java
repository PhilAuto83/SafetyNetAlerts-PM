package net.safety.alerts.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotBlank;
import net.safety.alerts.dto.ChildAlertDTO;
import net.safety.alerts.exceptions.AddressNotFoundException;
import net.safety.alerts.service.ChildAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@Validated
public class ChildAlertController {

    private static final Logger logger = LoggerFactory.getLogger(ChildAlertController.class);

    @Autowired
    private ChildAlertService childAlertService;

    @GetMapping("/childAlert")
    public ChildAlertDTO getChildrenListByAddress(@RequestParam("address") @NotBlank(message="address cannot be null or empty") String address) throws JsonProcessingException {
        logger.info("Request launched to get children list at address : {}", address);
        if(childAlertService.getAddressOccurrence(address)==0){
            logger.error("This address \"{}\" has no person related to it.", address);
            throw new AddressNotFoundException("This address '"+address+"' has no person related to it.");
        }
        return childAlertService.getPersonFromAddress(address);
    }
}
