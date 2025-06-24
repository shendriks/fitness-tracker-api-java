package dev.shendriks.fitnesstrackerapi.domain.application.mapper;

import dev.shendriks.fitnesstrackerapi.domain.application.dto.ApplicationRegisterRequest;
import dev.shendriks.fitnesstrackerapi.domain.application.dto.ApplicationRegisterResponse;
import dev.shendriks.fitnesstrackerapi.domain.application.dto.ApplicationResponse;
import dev.shendriks.fitnesstrackerapi.domain.application.entity.Application;
import dev.shendriks.fitnesstrackerapi.domain.application.enums.Category;
import dev.shendriks.fitnesstrackerapi.domain.developer.entity.Developer;
import dev.shendriks.fitnesstrackerapi.supportive.apikey.ApiKeyGenerator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationMapper {
    private final ApiKeyGenerator apiKeyGenerator;

    public ApplicationMapper(ApiKeyGenerator apiKeyGenerator) {
        this.apiKeyGenerator = apiKeyGenerator;
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
        application.setApiKey(apiKeyGenerator.generateApiKey());
        application.setDeveloper(developer);
        application.setCategory(Category.fromString(request.category()));

        return application;
    }

    public List<ApplicationResponse> toResponses(List<Application> applications) {
        return applications.stream().map(this::toResponse).toList();
    }
}
