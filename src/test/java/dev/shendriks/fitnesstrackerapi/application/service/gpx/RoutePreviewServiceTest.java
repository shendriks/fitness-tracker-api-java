package dev.shendriks.fitnesstrackerapi.application.service.gpx;

import dev.shendriks.fitnesstrackerapi.domain.value.GPSPositionData;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static java.time.Instant.EPOCH;
import static org.junit.jupiter.api.Assertions.*;

class RoutePreviewServiceTest {
    private final RoutePreviewService service = new RoutePreviewService();

    private static GPSPositionData pos(double lat, double lon) {
        return new GPSPositionData(EPOCH, lat, lon, Optional.empty());
    }

    /**
     * Asserts that the given byte array starts with the PNG signature 89 50 4E 47 0D 0A 1A 0A
     * (see <a href="https://www.w3.org/TR/png-3/#3PNGsignature">PNG Signature</a>)
     */
    private static void assertPNGSignature(byte[] bytes) {
        assertTrue(bytes.length > 8, "Expected PNG to be more than 8 bytes long");
        assertEquals((byte) 0x89, bytes[0], "Expected PNG signature's first byte to be 89");
        assertEquals((byte) 0x50, bytes[1], "Expected PNG signature's second byte to be 50");
        assertEquals((byte) 0x4E, bytes[2], "Expected PNG signature's third byte to be 4E");
        assertEquals((byte) 0x47, bytes[3], "Expected PNG signature's fourth byte to be 47");
        assertEquals((byte) 0x0D, bytes[4], "Expected PNG signature's fifth byte to be 0D");
        assertEquals((byte) 0x0A, bytes[5], "Expected PNG signature's sixth byte to be 0A");
        assertEquals((byte) 0x1A, bytes[6], "Expected PNG signature's seventh byte to be 1A");
        assertEquals((byte) 0x0A, bytes[7], "Expected PNG signature's eighth byte to be 0A");
    }

    @Test
    void createPreview_throwsWhenLessThanTwoPositions() {
        assertThrows(IllegalArgumentException.class, () -> service.createPreview(List.of(), 200, 100));
        assertThrows(IllegalArgumentException.class, () -> service.createPreview(List.of(pos(0.0, 0.1)), 200, 100));
    }

    @Test
    void createPreview_returnsValidPngWithRequestedDimensions() throws IOException {
        List<GPSPositionData> track = List.of(
            pos(0.0, 1.0),
            pos(0.1, 1.1),
            pos(0.2, 1.2)
        );

        String actualImageData = service.createPreview(track, 300, 150);
        assertNotNull(actualImageData, "Image data should not be null");
        byte[] actualBytes = Base64.getDecoder().decode(actualImageData);
        assertPNGSignature(actualBytes);
        BufferedImage actualImage = ImageIO.read(new ByteArrayInputStream(actualBytes));
        assertNotNull(actualImage, "Image should not be null");
        assertEquals(300, actualImage.getWidth(), "Expected image width to be 300");
        assertEquals(150, actualImage.getHeight(), "Expected image height to be 150");
    }

    @Test
    void createPreview_isDeterministicForSameInput() throws IOException {
        List<GPSPositionData> track = List.of(
            pos(0.0, 1.0),
            pos(0.1, 1.1),
            pos(0.2, 1.2),
            pos(0.3, 1.3)
        );

        String actualImageData1 = service.createPreview(track, 400, 300);
        String actualImageData2 = service.createPreview(track, 400, 300);
        assertEquals(actualImageData1, actualImageData2, "Expected preview image data to be the same for the same input");
    }

    @Test
    void createPreview_handlesDifferentAspectRatios() throws IOException {
        List<GPSPositionData> vertical = List.of(
            pos(0.0, 1.0),
            pos(0.1, 1.0),
            pos(0.2, 1.0)
        );
        List<GPSPositionData> horizontal = List.of(
            pos(0.0, 1.0),
            pos(0.0, 1.1),
            pos(0.0, 1.2)
        );

        String actualVerticalImageData = service.createPreview(vertical, 200, 400);
        String actualHorizontalImageData = service.createPreview(horizontal, 400, 200);

        byte[] actualVerticalBytes = Base64.getDecoder().decode(actualVerticalImageData);
        byte[] actualHorizontalBytes = Base64.getDecoder().decode(actualHorizontalImageData);
        assertPNGSignature(actualVerticalBytes);
        assertPNGSignature(actualHorizontalBytes);
        BufferedImage actualVerticalImage = ImageIO.read(new ByteArrayInputStream(actualVerticalBytes));
        BufferedImage actualHorizontalImage = ImageIO.read(new ByteArrayInputStream(actualHorizontalBytes));
        assertEquals(200, actualVerticalImage.getWidth());
        assertEquals(400, actualVerticalImage.getHeight());
        assertEquals(400, actualHorizontalImage.getWidth());
        assertEquals(200, actualHorizontalImage.getHeight());
        assertNotEquals(actualVerticalImageData, actualHorizontalImageData, "Expected different preview image data for different aspect ratios");
    }
}
