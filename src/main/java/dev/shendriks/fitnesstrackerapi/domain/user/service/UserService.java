package dev.shendriks.fitnesstrackerapi.domain.user.service;

import dev.shendriks.fitnesstrackerapi.domain.user.dto.UserResponse;
import dev.shendriks.fitnesstrackerapi.domain.user.dto.UserSignupRequest;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.user.mapper.UserMapper;
import dev.shendriks.fitnesstrackerapi.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public UserResponse save(UserSignupRequest request) {
        User user = mapper.toEntity(request);
        repository.save(user);
        
        return mapper.toResponse(user);
    }

    public UserResponse findDeveloperById(String id) {
        var developer = repository.findByUlid(id).orElse(null);
        if (developer == null) {
            return null;
        }
        
        return mapper.toResponse(developer); 
    }

    public UserResponse findDeveloperByEmail(String email) {
        var developer = repository.findByEmail(email).orElse(null);
        if (developer == null) {
            return null;
        }
        
        return mapper.toResponse(developer);
    }
}
