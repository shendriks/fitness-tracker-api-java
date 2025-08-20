package dev.shendriks.fitnesstrackerapi.infrastructure.supportive;

import lombok.extern.java.Log;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Optional;

@Log
@Component
public class Base64FileEncoder {
    public Optional<String> encodeFile(String path) {
        if (path == null || path.isBlank()) {
            return Optional.empty();
        }
        try {
            File resource = new ClassPathResource(path).getFile();
            return Optional.of(Base64.getEncoder().encodeToString(Files.readAllBytes(resource.toPath())));
        } catch (IOException e) {
            log.warning("Failed to read image file: %s".formatted(e.getMessage()));
            return Optional.empty();
        }
    }
}
