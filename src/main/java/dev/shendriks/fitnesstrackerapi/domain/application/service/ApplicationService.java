//package dev.shendriks.fitnesstrackerapi.domain.application.service;
//
//import dev.shendriks.fitnesstrackerapi.domain.application.dto.ApplicationRegisterRequest;
//import dev.shendriks.fitnesstrackerapi.domain.application.dto.ApplicationRegisterResponse;
//import dev.shendriks.fitnesstrackerapi.domain.application.entity.Application;
//import dev.shendriks.fitnesstrackerapi.domain.application.exception.ApplicationAlreadyExistsException;
//import dev.shendriks.fitnesstrackerapi.domain.application.mapper.ApplicationMapper;
//import dev.shendriks.fitnesstrackerapi.domain.application.repository.ApplicationRepository;
//import dev.shendriks.fitnesstrackerapi.domain.developer.entity.Developer;
//import dev.shendriks.fitnesstrackerapi.domain.developer.repository.DeveloperRepository;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ApplicationService {
//    private final DeveloperRepository developerRepository;
//    private final ApplicationRepository applicationRepository;
//    private final ApplicationMapper mapper;
//
//    public ApplicationService(
//        DeveloperRepository developerRepository, 
//        ApplicationRepository applicationRepository, 
//        ApplicationMapper mapper
//    ) {
//        this.developerRepository = developerRepository;
//        this.applicationRepository = applicationRepository;
//        this.mapper = mapper;
//    }
//
//    public ApplicationRegisterResponse register(ApplicationRegisterRequest request, UserDetails details) {
//        if (applicationRepository.existsByName((request.name()))) {
//            throw new ApplicationAlreadyExistsException();
//        }
//
//        Developer developer = developerRepository.findByEmail(details.getUsername()).orElseThrow();
//
//        Application application = mapper.toEntity(request, developer);
//        applicationRepository.save(application);
//
//        return mapper.toRegisterResponse(application);
//    }
//}
