package net.safety.alerts.service;


import net.safety.alerts.dao.PersonsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommunityEmailService {

    @Autowired
    private PersonsDAO personsDAO;

    public List<String> getEmailsFromCity(String city) {
        return new ArrayList<>();
    }
}
