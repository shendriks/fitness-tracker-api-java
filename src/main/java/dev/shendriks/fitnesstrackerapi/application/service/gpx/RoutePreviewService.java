package dev.shendriks.fitnesstrackerapi.application.service.gpx;

import dev.shendriks.fitnesstrackerapi.domain.value.GPSPositionData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.function.DoubleFunction;

@Slf4j
@Service
@AllArgsConstructor
public class RoutePreviewService {
    /**
     * Given GPS positions, create a preview image and save it to the file system. The preview is created using an
     * equirectangular projection (see <a href="https://en.wikipedia.org/wiki/Equirectangular_projection">Equirectangular Projection</a>).
     */
    public String createPreview(List<GPSPositionData> positions, int width, int height) throws IOException {
        if (positions.size() < 2) {
            throw new IllegalArgumentException("At least 2 coordinates required for preview");
        }

        double minLat = positions.stream().mapToDouble(GPSPositionData::latitude).min().orElseThrow();
        double maxLat = positions.stream().mapToDouble(GPSPositionData::latitude).max().orElseThrow();
        double minLon = positions.stream().mapToDouble(GPSPositionData::longitude).min().orElseThrow();
        double maxLon = positions.stream().mapToDouble(GPSPositionData::longitude).max().orElseThrow();

        double meanLatRad = Math.toRadians((minLat + maxLat) / 2.0);

        double latSpan = maxLat - minLat;
        double lonSpan = (maxLon - minLon) * Math.cos(meanLatRad);

        double targetRatio = (double) width / height;
        double dataRatio = lonSpan / latSpan;

        double scale;
        double xOffset;
        double yOffset;
        int padding = 10;

        if (dataRatio > targetRatio) {
            xOffset = 0;
            scale = (width - 2.0 * padding) / lonSpan;
            double scaledHeight = latSpan * scale;
            yOffset = (height - scaledHeight) / 2.0;
        } else {
            yOffset = 0;
            scale = (height - 2.0 * padding) / latSpan;
            double scaledWidth = lonSpan * scale;
            xOffset = (width - scaledWidth) / 2.0;
        }

        DoubleFunction<Integer> projectX = (lon) ->
            (int) (((lon - minLon) * Math.cos(meanLatRad)) * scale + xOffset + padding);

        DoubleFunction<Integer> projectY = (lat) ->
            (int) (((maxLat - lat)) * scale + yOffset + padding);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(214, 9, 1));
        graphics.setStroke(new BasicStroke(2f));

        Path2D path = new Path2D.Double();
        boolean first = true;
        for (GPSPositionData position : positions) {
            int x = projectX.apply(position.longitude());
            int y = projectY.apply(position.latitude());
            if (first) {
                path.moveTo(x, y);
                first = false;
                continue;
            }
            path.lineTo(x, y);
        }
        graphics.draw(path);
        graphics.dispose();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        return Base64.getEncoder().encodeToString(stream.toByteArray());
    }
}
