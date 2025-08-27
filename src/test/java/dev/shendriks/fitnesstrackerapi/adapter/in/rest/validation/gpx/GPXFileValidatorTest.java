package dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.gpx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class GPXFileValidatorTest {
    private GPXFileValidator validator;

    private static MultipartFile mockFile(String name, byte[] content) {
        return new MockMultipartFile("gpxFile", name, "application/gpx+xml", content);
    }

    @BeforeEach
    void setUp() {
        validator = new GPXFileValidator();
    }

    @Test
    void validate_nullFile_throwsFileIsEmpty() {
        GPXFileValidatorException ex = assertThrows(GPXFileValidatorException.class, () -> validator.validate(null));

        assertEquals("File is empty", ex.getMessage());
    }

    @Test
    void validate_emptyFile_throwsFileIsEmpty() {
        MultipartFile file = mockFile("track.gpx", new byte[0]);

        GPXFileValidatorException ex = assertThrows(GPXFileValidatorException.class, () -> validator.validate(file));
        assertEquals("File is empty", ex.getMessage());
    }

    @Test
    void validate_fileTooBig_throwsFileIsTooBig() {
        byte[] content = new byte[10 * 1024 * 1024 + 1];
        MultipartFile file = mockFile("track.gpx", content);

        GPXFileValidatorException ex = assertThrows(GPXFileValidatorException.class, () -> validator.validate(file));
        assertEquals("File is too big", ex.getMessage());
    }

    @Test
    void validate_wrongSuffix_throwsWrongSuffix() {
        MultipartFile file = mockFile("track.xml", "some content".getBytes());

        GPXFileValidatorException ex = assertThrows(GPXFileValidatorException.class, () -> validator.validate(file));
        assertEquals("The gpxFile must be a GPX gpxFile (.gpx)", ex.getMessage());
    }

    @Test
    void validate_invalidXml_throwsNoValidXml() {
        MultipartFile file = mockFile("track.gpx", "<invalid-xml>".getBytes());

        GPXFileValidatorException ex = assertThrows(GPXFileValidatorException.class, () -> validator.validate(file));
        assertEquals("The gpxFile is no valid XML gpxFile", ex.getMessage());
    }

    @Test
    void validate_wrongVersion_throwsWrongVersion() {
        String content = """
            <?xml version="1.0" encoding="UTF-8"?>
            <gpx version="1.0" creator="test" xmlns="http://www.topografix.com/GPX/1/1">"
                <trk>
                    <name>some-name</name>
                    <trkseg>
                        <trkpt lat="0" lon="0">
                            <time>2025-08-22T00:00:00Z</time>
                        </trkpt>
                        <trkpt lat="1" lon="1">
                            <time>2025-08-22T00:01:00Z</time>
                        </trkpt>
                    </trkseg>
                </trk>
            </gpx>
            """;
        MultipartFile file = mockFile("track.gpx", content.getBytes());

        GPXFileValidatorException ex = assertThrows(GPXFileValidatorException.class, () -> validator.validate(file));
        assertEquals("The gpxFile must be a GPX 1.1 gpxFile", ex.getMessage());
    }

    @Test
    void validate_noTrack_throwsContainsNoTrack() {
        String content = """
            <?xml version="1.0" encoding="UTF-8"?>
            <gpx version="1.1" creator="test" xmlns="http://www.topografix.com/GPX/1/1">
                <metadata>
                    <name>No Track</name>
                </metadata>
            </gpx>
            """;
        MultipartFile file = mockFile("track.gpx", content.getBytes());

        GPXFileValidatorException ex = assertThrows(GPXFileValidatorException.class, () -> validator.validate(file));
        assertEquals("The gpxFile must contain at least one track", ex.getMessage());
    }

    @Test
    void validate_tooFewTrackPoints_throwsTooFewTrackPoints() {
        String content = """
            <?xml version="1.0" encoding="UTF-8"?>
            <gpx version="1.1" creator="test" xmlns="http://www.topografix.com/GPX/1/1">
                <trk>
                    <trkseg>
                        <trkpt lat="0" lon="0">
                            <time>2025-08-22T00:00:00Z</time>
                        </trkpt>
                    </trkseg>
                </trk>
            </gpx>
            """;
        MultipartFile file = mockFile("track.gpx", content.getBytes());

        GPXFileValidatorException ex = assertThrows(GPXFileValidatorException.class, () -> validator.validate(file));
        assertEquals("The gpxFile must contain at least two track points", ex.getMessage());
    }

    @Test
    void validate_missingTimeOnAnyPoint_throwsTimeElementIsMissing() {
        String content = """
            <?xml version="1.0" encoding="UTF-8"?>
            <gpx version="1.1" creator="test" xmlns="http://www.topografix.com/GPX/1/1">
                <trk>
                    <name>Test Track</name>
                    <trkseg>
                        <trkpt lat="0" lon="0">
                            <time>2025-08-22T00:00:00Z</time>
                        </trkpt>
                        <trkpt lat="1" lon="1">
                        </trkpt>
                    </trkseg>
                </trk>
            </gpx>
            """;
        MultipartFile file = mockFile("track.gpx", content.getBytes());

        GPXFileValidatorException ex = assertThrows(GPXFileValidatorException.class, () -> validator.validate(file));
        assertEquals("The gpxFile contains at least one track point with no time element", ex.getMessage());
    }

    @Test
    void validate_invalidLatLon_throwsInvalidLatLon() {
        String content = """
            <?xml version="1.0" encoding="UTF-8"?>
            <gpx version="1.1" creator="test" xmlns="http://www.topografix.com/GPX/1/1">
                <trk>
                    <name>Test Track</name>
                    <trkseg>
                        <trkpt lat="0" lon="0">
                            <time>2025-08-22T00:00:00Z</time>
                        </trkpt>
                        <trkpt lat="abc" lon="1">
                            <time>2025-08-22T00:00:01Z</time>
                        </trkpt>
                    </trkseg>
                </trk>
            </gpx>
            """;
        MultipartFile file = mockFile("track.gpx", content.getBytes());

        GPXFileValidatorException ex = assertThrows(GPXFileValidatorException.class, () -> validator.validate(file));
        assertEquals("The gpxFile contains invalid latitude or longitude values", ex.getMessage());
    }

    @Test
    void validate_validFile_passes() {
        String content = """
            <?xml version="1.0" encoding="UTF-8"?>
            <gpx version="1.1" creator="test" xmlns="http://www.topografix.com/GPX/1/1">
                <trk>
                    <name>Test Track</name>
                    <trkseg>
                        <trkpt lat="0" lon="0">
                            <time>2025-08-22T00:00:00Z</time>
                        </trkpt>
                        <trkpt lat="1" lon="1">
                            <time>2025-08-22T00:00:01Z</time>
                        </trkpt>
                    </trkseg>
                </trk>
            </gpx>
            """;
        MultipartFile file = mockFile("track.gpx", content.getBytes());

        assertDoesNotThrow(() -> validator.validate(file));
    }
}
