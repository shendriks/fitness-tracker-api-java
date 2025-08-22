package dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.gpx;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.IntStream;

@Log
@Service
public class GPXFileValidator {
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static DocumentBuilder documentBuilder;

    public GPXFileValidator() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw GPXFileValidatorException.becauseFileIsEmpty();
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw GPXFileValidatorException.becauseFileIsTooBig();
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".gpx")) {
            throw GPXFileValidatorException.becauseWrongSuffix();
        }

        Document document;
        try {
            document = documentBuilder.parse(file.getInputStream());
        } catch (SAXException | IOException e) {
            throw GPXFileValidatorException.becauseFileIsNoValidXMLFile();
        }

        document.getDocumentElement().normalize();

        Element gpx = (Element) Optional.of(document.getElementsByTagName("gpx")).orElseThrow().item(0);
        String version = gpx.getAttribute("version");
        if (!"1.1".equals(version)) {
            throw GPXFileValidatorException.becauseWrongVersion();
        }

        if (document.getElementsByTagName("trk").getLength() == 0) {
            throw GPXFileValidatorException.becauseFileContainsNoTrack();
        }

        NodeList trackPoints = document.getElementsByTagName("trkpt");
        if (trackPoints.getLength() < 2) {
            throw GPXFileValidatorException.becauseFileContainsTooFewTrackPoints();
        }

        IntStream
            .range(0, trackPoints.getLength())
            .mapToObj(i -> (Element) trackPoints.item(i)).forEach(element -> {
                if (element.getElementsByTagName("time").getLength() == 0) {
                    throw GPXFileValidatorException.becauseTimeElementIsMissing();
                }

                String latStr = element.getAttribute("lat");
                String lonStr = element.getAttribute("lon");
                try {
                    Double.parseDouble(latStr);
                    Double.parseDouble(lonStr);
                } catch (NumberFormatException ignored) {
                    throw GPXFileValidatorException.becauseInvalidLatitudeLongitudeValues();
                }
            });
    }
}
