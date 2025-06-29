package dev.shendriks.fitnesstrackerapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static dev.shendriks.fitnesstrackerapi.security.SecurityRequirementName.*;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(
    title = "Fitness API",
    version = "0.0.1",
    description = "A simple API for tracking fitness activities"
))
@SecuritySchemes(value = {
    @SecurityScheme(
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        name = BEARER_TOKEN,
        paramName = "Authorization"
    ),
    @SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = BASIC_AUTH,
        scheme = "Basic"
    )
})
public class FitnessTrackerApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(
            FitnessTrackerApiApplication.class, 
            args
        );
    }
}
