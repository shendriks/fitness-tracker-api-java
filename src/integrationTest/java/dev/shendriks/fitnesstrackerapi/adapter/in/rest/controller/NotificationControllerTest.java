package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shendriks.fitnesstrackerapi.AccessTokenHelper;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.NotificationDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.NotificationDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NotificationControllerTest {
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final NotificationDbEntityRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationControllerTest(
        @Autowired MockMvc mvc,
        @Autowired AccessTokenHelper accessTokenHelper,
        @Autowired NotificationDbEntityRepository notificationRepository,
        @Autowired UserRepository userRepository
    ) {
        this.mvc = mvc;
        this.accessTokenHelper = accessTokenHelper;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Test
    void getNotifications_withoutSinceId_returnsAllForUser_inDescendingOrder() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();
        notificationRepository.save(
            NotificationDbEntity
                .builder()
                .user(user)
                .title("Welcome")
                .description("Welcome to the app")
                .build());
        notificationRepository.save(
            NotificationDbEntity
                .builder()
                .user(user)
                .title("Update")
                .description("We have updated our terms")
                .build()
        );

        String token = accessTokenHelper.getAccessToken(userUlid);
        String json = mvc.perform(
                get("/api/notifications")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> actualNotifications = mapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(2, actualNotifications.size(), "Expected two notifications");
        assertEquals("Update", actualNotifications.getFirst().get("title"));
        assertEquals("We have updated our terms", actualNotifications.getFirst().get("description"));
        assertEquals("Welcome", actualNotifications.get(1).get("title"));
        assertEquals("Welcome to the app", actualNotifications.get(1).get("description"));
    }

    @Test
    void getNotifications_withSinceId_returnsOnlyNewer_inDescendingOrder() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();

        NotificationDbEntity notification1 = notificationRepository.save(
            NotificationDbEntity.builder().user(user).title("N1").description("D1").build()
        );
        notificationRepository.save(NotificationDbEntity.builder().user(user).title("N2").description("D2").build());
        notificationRepository.save(NotificationDbEntity.builder().user(user).title("N3").description("D3").build());

        String token = accessTokenHelper.getAccessToken(userUlid);

        String json = mvc.perform(
                get("/api/notifications")
                    .param("sinceId", notification1.getUlid())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        // Assert: expect n3 then n2
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> list = mapper.readValue(json, new TypeReference<>() {
        });
        assertEquals(2, list.size(), "Expected two notifications");
        assertEquals("N3", list.get(0).get("title"));
        assertEquals("D3", list.get(0).get("description"));
        assertEquals("N2", list.get(1).get("title"));
        assertEquals("D2", list.get(1).get("description"));
    }
}
