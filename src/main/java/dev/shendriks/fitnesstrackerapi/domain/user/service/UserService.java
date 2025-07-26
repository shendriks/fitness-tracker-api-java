package dev.shendriks.fitnesstrackerapi.domain.user.service;

import dev.shendriks.fitnesstrackerapi.domain.user.dto.UserResponse;
import dev.shendriks.fitnesstrackerapi.domain.user.dto.UserSignupRequest;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.user.exception.EmailAlreadyRegisteredException;
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

    public UserResponse signUp(UserSignupRequest request) {
        String email = request.email().toLowerCase().trim();
        UserResponse user = findUserByEmail(email);
        if (user != null) {
            throw new EmailAlreadyRegisteredException();
        }

        return save(request);
    }
    
    public UserResponse save(UserSignupRequest request) {
        User user = mapper.toEntity(request);
        repository.save(user);
        
        return mapper.toResponse(user);
    }

    public UserResponse findUserById(String id) {
        var user = repository.findByUlid(id).orElse(null);
        if (user == null) {
            return null;
        }
        
        return mapper.toResponse(user); 
    }

    public UserResponse findUserByEmail(String email) {
        var user = repository.findByEmail(email).orElse(null);
        if (user == null) {
            return null;
        }
        
        return mapper.toResponse(user);
    }
}
