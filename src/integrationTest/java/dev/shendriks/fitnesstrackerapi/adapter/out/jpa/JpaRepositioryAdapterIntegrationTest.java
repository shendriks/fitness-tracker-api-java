package dev.shendriks.fitnesstrackerapi.adapter.out.jpa;

import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

public class JpaRepositioryAdapterIntegrationTest {
    protected final TestEntityManager entityManager;

    public JpaRepositioryAdapterIntegrationTest(@Autowired TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected UserId createAndPersistUser() {
        UserDbEntity user = UserDbEntity
            .builder()
            .name("Alice")
            .email("alice@example.com")
            .password("password")
            .authority("ROLE_USER")
            .accountType(AccountType.BASIC)
            .build();
        Long userId = entityManager.persistAndGetId(user, Long.class);
        return new UserId(userId);
    }
}
