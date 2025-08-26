package dev.shendriks.fitnesstrackerapi;

import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BASIC_AUTH;
import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@SpringBootApplication
@EnableScheduling
@EnableAsync
//@EnableSpringDataWebSupport
@OpenAPIDefinition(info = @Info(
    title = "Fitness API",
    version = "0.0.1",
    description = "A simple API for tracking fitness activities"),
    tags = {
        @Tag(name = OpenApiTagName.USERS, description = "A user can sign up, read and write activities, etc."),
        @Tag(name = OpenApiTagName.ACCESS_TOKENS, description = "An access token is needed to access secured endpoints"),
        @Tag(name = OpenApiTagName.ACTIVITIES, description = "An activity is a record of a user's exercise, e.g. running, walking or swimming"),
        @Tag(name = OpenApiTagName.CHALLENGES, description = "Users can master challenges and earn trophies"),
        @Tag(name = OpenApiTagName.CHALLENGE_PARTICIPATIONS, description = "Challenges the user is participating in"),
        @Tag(name = OpenApiTagName.MILESTONES, description = "Users can reach milestones by uploading activities"),
        @Tag(name = OpenApiTagName.TROPHIES, description = "Trophies can be earned by reaching milestones or completing challenges"),
        @Tag(name = OpenApiTagName.NOTIFICATIONS, description = "Notifications about events, such as activity uploaded, or trophy earned"),
        @Tag(name = OpenApiTagName.PING, description = "Health check")
    })
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
