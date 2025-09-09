package dev.shendriks.fitnesstrackerapi.domain.service.gpx;

import dev.shendriks.fitnesstrackerapi.domain.value.GPSPositionData;
import dev.shendriks.fitnesstrackerapi.domain.value.ImageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class RoutePreviewService {
    /**
     * Creates a PNG image that previews the route described by the given GPS positions, using equirectangular
     * projection.
     *
     * @param positions List of GPS positions containing latitude and longitude coordinates that define the track
     * @param width     The width of the output image in pixels
     * @param height    The height of the output image in pixels
     * @return ImageData containing the PNG image bytes of the rendered track preview, or null if an error occurred
     */
    public ImageData createPreview(List<GPSPositionData> positions, int width, int height) {
        if (positions == null || positions.size() < 2) {
            throw new IllegalArgumentException("At least two GPS positions are required to render a route preview");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }

        double minLat = positions.stream().mapToDouble(GPSPositionData::latitude).min().orElseThrow();
        double maxLat = positions.stream().mapToDouble(GPSPositionData::latitude).max().orElseThrow();
        double minLon = positions.stream().mapToDouble(GPSPositionData::longitude).min().orElseThrow();
        double maxLon = positions.stream().mapToDouble(GPSPositionData::longitude).max().orElseThrow();

        /*
         * Equirectangular projection
         *
         * - x = lon * cos(phi0), where phi0 is the track's mid-latitude
         * - y = lat
         *
         *  Latitude correction accounts for the fact that degrees of longitude represent smaller physical distances as
         *  you move away from the equator. Meridians converge toward the poles, so one degree of longitude spans:
         *
         *  - ~111.32 km at the equator (cos 0° = 1)
         *  - ~55.8 km at 60° latitude (cos 60° = 0.5)
         *
         *  To reduce distortion when plotting a geographic track on a flat image, you scale the longitudinal coordinate
         *  by cos(phi), where phi is a representative latitude.
         */
        double phi0 = (minLat + maxLat) / 2.0;
        double cosPhi = Math.cos(Math.toRadians(phi0));
        double minX = minLon * cosPhi;
        double maxX = maxLon * cosPhi;
        double minY = minLat;
        double maxY = maxLat;

        double trackWidth = Math.max(maxX - minX, Double.MIN_VALUE);
        double trackHeight = Math.max(maxY - minY, Double.MIN_VALUE);

        double padding = 10;
        double drawableW = Math.max(1.0, width - 2 * padding);
        double drawableH = Math.max(1.0, height - 2 * padding);

        double scaleX = drawableW / trackWidth;
        double scaleY = drawableH / trackHeight;
        double scale = Math.min(scaleX, scaleY);

        double scaledW = trackWidth * scale;
        double scaledH = trackHeight * scale;
        double offsetX = (width - scaledW) / 2.0 - minX * scale;
        double offsetY = (height - scaledH) / 2.0 - minY * scale;

        Path2D path = new Path2D.Double();
        boolean isFirst = true;
        for (GPSPositionData position : positions) {
            double x = position.longitude() * cosPhi * scale + offsetX;
            double y = position.latitude() * scale + offsetY;
            double yFlipped = height - y;
            if (isFirst) {
                path.moveTo(x, yFlipped);
                isFirst = false;
                continue;
            }
            path.lineTo(x, yFlipped);
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(new Color(214, 9, 1));
        graphics.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.draw(path);
        graphics.dispose();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", stream);
        } catch (IOException e) {
            log.error("Failed to write PNG image", e);
            return null;
        }

        return new ImageData(stream.toByteArray());
    }
}
