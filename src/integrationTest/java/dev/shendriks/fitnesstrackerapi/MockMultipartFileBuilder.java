package dev.shendriks.fitnesstrackerapi;

import org.springframework.mock.web.MockMultipartFile;

public class MockMultipartFileBuilder {
    private String name = "";
    private String originalFilename = "";
    private String contentType = "";
    private byte[] content = new byte[0];

    public MockMultipartFileBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MockMultipartFileBuilder withOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
        return this;
    }

    public MockMultipartFileBuilder withContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public MockMultipartFileBuilder withContent(byte[] content) {
        this.content = content;
        return this;
    }

    public MockMultipartFile build() {
        return new MockMultipartFile(name, originalFilename, contentType, content);
    }
}
