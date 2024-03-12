package net.safety.alerts.configuration;


import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpTraceConfig {

    @Bean
    public HttpExchangeRepository configActuatorTrace(){
        return new InMemoryHttpExchangeRepository();
    }
}
