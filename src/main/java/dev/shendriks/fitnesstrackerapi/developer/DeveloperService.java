package dev.shendriks.fitnesstrackerapi.developer;

import org.springframework.stereotype.Service;

@Service
public class DeveloperService {
    private final DeveloperRepository repository;
    private final DeveloperMapper mapper;

    public DeveloperService(DeveloperRepository repository, DeveloperMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public DeveloperResponse save(DeveloperSignupRequest request) {
        Developer developer = mapper.toEntity(request);
        repository.save(developer);
        
        return mapper.toResponse(developer);
    }

    public DeveloperResponse findDeveloperById(String id) {
        var developer = repository.findById(Long.parseLong(id)).orElse(null);
        if (developer == null) {
            return null;
        }
        
        return mapper.toResponse(developer); 
    }

    public DeveloperResponse findDeveloperByEmail(String email) {
        var developer = repository.findByEmail(email).orElse(null);
        if (developer == null) {
            return null;
        }
        
        return mapper.toResponse(developer);
    }
}
