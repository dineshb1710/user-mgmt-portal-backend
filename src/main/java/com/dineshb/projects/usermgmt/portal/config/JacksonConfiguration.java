package com.dineshb.projects.usermgmt.portal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.TimeZone;

@Configuration
public class JacksonConfiguration {

    @Bean
    ObjectMapper jacksonObjectMapper() {
        return new Jackson2ObjectMapperBuilder().createXmlMapper(false)
                // Set timezone for JSON serialization as system timezone
                .timeZone(TimeZone.getDefault())
                .build();
    }
}
