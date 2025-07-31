package dev.shendriks.fitnesstrackerapi.domain.notification.service;

import com.sun.source.tree.ReturnTree;
import dev.shendriks.fitnesstrackerapi.domain.notification.dto.NotificationResponse;
import dev.shendriks.fitnesstrackerapi.domain.notification.exception.NotificationNotFoundException;
import dev.shendriks.fitnesstrackerapi.domain.notification.mapper.NotificationMapper;
import dev.shendriks.fitnesstrackerapi.domain.notification.repository.NotificationRepository;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    public NotificationService(NotificationRepository repository, NotificationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Iterable<NotificationResponse> findAllByUser(User user) {
        var notifications = repository.findAllByUserOrderByIdDesc(user);
        return mapper.toResponses(notifications);
    }    
    
    public Iterable<NotificationResponse> findAllByUserSince(User user, String ulid) {
        var notification = repository.findByUlid(ulid).orElseThrow(() -> new NotificationNotFoundException(ulid));
        var notifications = repository.findByUserAndIdGreaterThanOrderByIdDesc(user, notification.getId());
        return mapper.toResponses(notifications);
    }
}
