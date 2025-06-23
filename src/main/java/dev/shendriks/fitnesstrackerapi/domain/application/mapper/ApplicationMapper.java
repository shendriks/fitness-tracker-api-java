package dev.shendriks.fitnesstrackerapi.domain.application.mapper;

import dev.shendriks.fitnesstrackerapi.domain.application.dto.ApplicationRegisterRequest;
import dev.shendriks.fitnesstrackerapi.domain.application.dto.ApplicationRegisterResponse;
import dev.shendriks.fitnesstrackerapi.domain.application.dto.ApplicationResponse;
import dev.shendriks.fitnesstrackerapi.domain.application.enums.Category;
import dev.shendriks.fitnesstrackerapi.domain.application.entity.Application;
import dev.shendriks.fitnesstrackerapi.domain.developer.entity.Developer;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;

import java.util.HexFormat;
import java.util.List;

@Component
public class ApplicationMapper {
    private static String generateApiKey() {
        byte[] key = KeyGenerators.secureRandom(64).generateKey();
        return HexFormat.of().formatHex(key);
    }

    public ApplicationRegisterResponse toRegisterResponse(Application application) {
        return new ApplicationRegisterResponse(
            application.getUlid(),
            application.getName(), 
            application.getApiKey(),
            application.getCategory()
        );
    }

    public ApplicationResponse toResponse(Application application) {
        return new ApplicationResponse(
                application.getUlid(),
                application.getName(),
                application.getDescription(),
                application.getCategory(),
                application.getApiKey());
    }

    public Application toEntity(ApplicationRegisterRequest request, Developer developer) {

        Application application = new Application();
        application.setName(request.name().trim());
        application.setDescription(request.description().trim());
        application.setApiKey(generateApiKey());
        application.setDeveloper(developer);
        application.setCategory(Category.fromString(request.category()));

        return application;
    }

    public List<ApplicationResponse> toResponses(List<Application> applications) {
        return applications.stream().map(this::toResponse).toList();
    }
}
