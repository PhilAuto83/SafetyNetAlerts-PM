package net.safety.alerts.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.safety.alerts.model.AlertsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;


public class AlertsDAO {

    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final Logger logger = LoggerFactory.getLogger(AlertsDAO.class);

    public static AlertsData getData()  {
        AlertsData alertsData = null;
        try {
            alertsData = mapper.readValue(new File("src/main/resources/data.json"), AlertsData.class);
            return alertsData;
        } catch (IOException e) {
           logger.error(e.toString());
        }
        return alertsData;
    }

    public static void save(AlertsData alertsData){
        logger.info("Write AlertsData Object to file");
        try {
            logger.debug("AlertsData object contains a list of persons :"+alertsData.getPersons());
            logger.debug("AlertsData object contains a list of fire stations :"+alertsData.getFireStations());
            logger.debug("AlertsData object contains a list of medical records :"+alertsData.getMedicalRecords());
            mapper.writeValue(new File("src/main/resources/data.json"),alertsData);
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

}
