package dev.shendriks.fitnesstrackerapi.developer.mapper;

import dev.shendriks.fitnesstrackerapi.application.mapper.ApplicationMapper;
import dev.shendriks.fitnesstrackerapi.application.repository.ApplicationRepository;
import dev.shendriks.fitnesstrackerapi.developer.dto.DeveloperResponse;
import dev.shendriks.fitnesstrackerapi.developer.dto.DeveloperSignupRequest;
import dev.shendriks.fitnesstrackerapi.developer.entity.Developer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DeveloperMapper {
    private final PasswordEncoder passwordEncoder;
    private final ApplicationMapper applicationMapper;
    private final ApplicationRepository applicationRepository;

    public DeveloperMapper(
            PasswordEncoder passwordEncoder,
            ApplicationMapper applicationMapper,
            ApplicationRepository applicationRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.applicationMapper = applicationMapper;
        this.applicationRepository = applicationRepository;
    }

    public DeveloperResponse toResponse(Developer developer) {
        var applications = applicationRepository.findByDeveloperOrderByIdDesc(developer);
        var applicationResponses = applicationMapper.toResponses(applications);
        return new DeveloperResponse(developer.getId(), developer.getEmail(), applicationResponses);
    }

    public Developer toEntity(DeveloperSignupRequest request) {
        Developer developer = new Developer();
        developer.setEmail(request.email().trim());
        developer.setPassword(passwordEncoder.encode(request.password()));
        developer.setAuthority("ROLE_DEVELOPER");
        return developer;
    }
}
