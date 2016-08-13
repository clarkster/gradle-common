package com.demo;

import com.demo.error.OrionErrorController;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
public class OrionAutoConfiguration {

    @Bean
    @Primary
    public OrionErrorController errorController(ServerProperties serverProperties, ErrorAttributes errorAttributes) {
        return new OrionErrorController(serverProperties, errorAttributes);
    }

}
