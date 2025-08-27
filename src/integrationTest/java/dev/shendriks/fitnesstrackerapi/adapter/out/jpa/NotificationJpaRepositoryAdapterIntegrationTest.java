package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.NotificationDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.entity.Notification;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationCreationData;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationId;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureTestEntityManager
class NotificationJpaRepositoryAdapterIntegrationTest extends JpaRepositioryAdapterIntegrationTest {
    private final NotificationJpaRepositoryAdapter adapter;

    public NotificationJpaRepositoryAdapterIntegrationTest(
        @Autowired NotificationJpaRepositoryAdapter adapter,
        @Autowired TestEntityManager entityManager
    ) {
        super(entityManager);
        this.adapter = adapter;
    }

    @Test
    void saveThenFindAll_worksAsExpected() {
        UserId userId = createAndPersistUser();

        NotificationCreationData notification1 = NotificationCreationData
            .builder()
            .userId(userId)
            .title("Welcome")
            .description("Welcome to the app")
            .build();
        NotificationCreationData notification2 = NotificationCreationData
            .builder()
            .userId(userId)
            .title("Second")
            .description("Second notification")
            .build();

        Notification actualSavedNotification1 = adapter.save(notification1);
        Notification actualSavedNotification2 = adapter.save(notification2);

        assertInstanceOf(NotificationId.class, actualSavedNotification1.id());
        assertInstanceOf(NotificationUlid.class, actualSavedNotification1.ulid());
        assertEquals("Welcome", actualSavedNotification1.title());
        assertEquals("Welcome to the app", actualSavedNotification1.description());
        assertInstanceOf(NotificationId.class, actualSavedNotification2.id());
        assertInstanceOf(NotificationUlid.class, actualSavedNotification2.ulid());
        assertEquals("Second", actualSavedNotification2.title());
        assertEquals("Second notification", actualSavedNotification2.description());

        List<Notification> actualNotifications = adapter.findAllByUser(userId);
        assertEquals(2, actualNotifications.size());

        // order by id descending
        assertEquals(actualSavedNotification2.id(), actualNotifications.get(0).id());
        assertEquals(actualSavedNotification1.id(), actualNotifications.get(1).id());
        NotificationDbEntity db1 = entityManager.find(NotificationDbEntity.class, actualSavedNotification1.id().value());
        NotificationDbEntity db2 = entityManager.find(NotificationDbEntity.class, actualSavedNotification2.id().value());
        assertEquals(actualNotifications.get(0).id().value(), db2.getId());
        assertEquals(actualNotifications.get(1).id().value(), db1.getId());
    }

    @Test
    void findByUserSinceUlid_returnsOnlyNewer() {
        UserId userId = createAndPersistUser();

        Notification notification1 = adapter.save(new NotificationCreationData(userId, "N1", "D1"));
        Notification notification2 = adapter.save(new NotificationCreationData(userId, "N2", "D2"));
        Notification notification3 = adapter.save(new NotificationCreationData(userId, "N3", "D3"));

        List<Notification> actualNewNotifications = adapter.findByUserSinceUlid(userId, notification1.ulid());

        assertEquals(2, actualNewNotifications.size());
        assertEquals(notification3.id(), actualNewNotifications.get(0).id());
        assertEquals(notification2.id(), actualNewNotifications.get(1).id());

        List<Notification> none = adapter.findByUserSinceUlid(userId, notification3.ulid());

        assertTrue(none.isEmpty());
    }
}
