package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shendriks.fitnesstrackerapi.AccessTokenHelper;
import dev.shendriks.fitnesstrackerapi.MockMultipartFileBuilder;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.ActivityDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.entity.UserDbEntity;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.ActivityDbEntityRepository;
import dev.shendriks.fitnesstrackerapi.adapter.out.jpa.repository.UserRepository;
import dev.shendriks.fitnesstrackerapi.infrastructure.Constant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostActivityUploadControllerTest {
    private final MockMvc mvc;
    private final AccessTokenHelper accessTokenHelper;
    private final UserRepository userRepository;
    private final ActivityDbEntityRepository activityRepository;

    public PostActivityUploadControllerTest(
        @Autowired MockMvc mvc,
        @Autowired AccessTokenHelper accessTokenHelper,
        @Autowired UserRepository userRepository,
        @Autowired ActivityDbEntityRepository activityRepository
    ) {
        this.mvc = mvc;
        this.accessTokenHelper = accessTokenHelper;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    private static MockMultipartFile buildValidGpxFileWithName() {
        String xml = """
            <gpx version="1.1" creator="integration-test" xmlns="http://www.topografix.com/GPX/1/1">
                <metadata>
                    <name>My Activity</name>
                    <time>2025-08-23T10:00:00Z</time>
                </metadata>
                <trk>
                    <trkseg>
                        <trkpt lat="0.1" lon="0.2">
                            <time>2025-08-23T10:00:00Z</time>
                        </trkpt>
                        <trkpt lat="0.3" lon="0.4">
                            <time>2025-08-23T10:05:00Z</time>
                        </trkpt>
                    </trkseg>
                </trk>
            </gpx>
            """;

        return new MockMultipartFileBuilder()
            .withName("gpxFile")
            .withOriginalFilename("track.gpx")
            .withContentType("application/gpx+xml")
            .withContent(xml.getBytes(StandardCharsets.UTF_8))
            .build();
    }

    @Test
    void uploadActivity_validRequest_persistsActivityAndReturnsDetails() throws Exception {
        String userUlid = "USER0000000000000000000000";
        UserDbEntity user = userRepository.findByUlid(userUlid).orElseThrow();
        MockMultipartFile gpxFile = buildValidGpxFileWithName();

        String token = accessTokenHelper.getAccessToken(userUlid);
        String json = mvc.perform(
                multipart("/api/activities/upload")
                    .file(gpxFile)
                    .param("activityType", "cycling")
                    .param("title", "Commute")
                    .param("description", "To work")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header("Authorization", "Bearer " + token)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = mapper.readValue(json, new TypeReference<>() {
        });
        assertNotNull(response.get("id"));
        assertEquals("cycling", response.get("activityType"));
        assertEquals("Commute", response.get("title"));
        assertEquals("To work", response.get("description"));
        assertTrue(((Number) response.get("distance")).intValue() > 0);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> gps = (List<Map<String, Object>>) response.get("gpsPositions");
        assertNotNull(gps);
        assertEquals(2, gps.size(), "Expected two GPS positions in response");

        List<ActivityDbEntity> activities = activityRepository.findAllByUserIdOrderByUlidDesc(user.getId());
        assertEquals(1, activities.size(), "Expected one activity persisted");
        ActivityDbEntity activityDbEntity = activities.getFirst();
        assertEquals("Commute", activityDbEntity.getTitle());
        assertEquals("To work", activityDbEntity.getDescription());
        assertEquals(user.getId(), activityDbEntity.getUser().getId());
        assertNotNull(activityDbEntity.getGpsPositions());
        assertEquals(2, activityDbEntity.getGpsPositions().size(), "Expected two GPS positions persisted");
        assertEquals(0.1, activityDbEntity.getGpsPositions().getFirst().getLatitude(), Constant.EPSILON);
        assertEquals(0.2, activityDbEntity.getGpsPositions().getFirst().getLongitude(), Constant.EPSILON);
        assertEquals(Instant.parse("2025-08-23T10:00:00Z"), activityDbEntity.getGpsPositions().getFirst().getTimestamp());
        assertEquals(0.3, activityDbEntity.getGpsPositions().get(1).getLatitude(), Constant.EPSILON);
        assertEquals(0.4, activityDbEntity.getGpsPositions().get(1).getLongitude(), Constant.EPSILON);
        assertEquals(Instant.parse("2025-08-23T10:05:00Z"), activityDbEntity.getGpsPositions().get(1).getTimestamp());
    }

    @Test
    void uploadActivity_withoutAuth_returns401() throws Exception {
        MockMultipartFile gpxFile = buildValidGpxFileWithName();

        mvc.perform(
                multipart("/api/activities/upload")
                    .file(gpxFile)
                    .param("activityType", "walking")
                    .param("title", "Walk")
                    .param("description", "No token")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadActivity_withInvalidGpxSuffix_returns400() throws Exception {
        String userUlid = "USER0000000000000000000000";
        MockMultipartFile invalidGpxFile = new MockMultipartFileBuilder()
            .withName("gpxFile")
            .withOriginalFilename("not-a-gpx.txt")
            .withContentType("text/plain")
            .withContent("not xml".getBytes(StandardCharsets.UTF_8))
            .build();

        String token = accessTokenHelper.getAccessToken(userUlid);
        mvc.perform(
                multipart("/api/activities/upload")
                    .file(invalidGpxFile)
                    .param("activityType", "running")
                    .param("title", "Bad Upload")
                    .param("description", "Wrong suffix")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header("Authorization", "Bearer " + token)
            )
            .andExpect(status().isBadRequest());
    }
}
