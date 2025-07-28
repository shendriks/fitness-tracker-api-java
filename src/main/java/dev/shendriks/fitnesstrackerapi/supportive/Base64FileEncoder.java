package dev.shendriks.fitnesstrackerapi.supportive;

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
    public Optional<String> encodeFile(String filePath) {
        Optional<String> imageData = Optional.empty();
        try {
            File resource = new ClassPathResource(filePath).getFile();
            imageData = Optional.of(Base64.getEncoder().encodeToString(Files.readAllBytes(resource.toPath())));
        } catch (IOException e) {
            log.warning("Failed to read image file: %s".formatted(e.getMessage()));
        }
        return imageData;
    }
}
