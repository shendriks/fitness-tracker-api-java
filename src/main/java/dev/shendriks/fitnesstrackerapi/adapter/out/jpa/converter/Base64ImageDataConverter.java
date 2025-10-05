package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter;

import dev.shendriks.fitnesstrackerapi.domain.value.ImageData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Base64ImageDataConverter is a JPA AttributeConverter implementation that
 * facilitates the conversion between the ImageData domain object and the
 * byte[] database representation.
 *
 * This converter is intended for use with entities that store image data in 
 * their database schema. The image data is stored as a byte array (typically as a BLOB).
 *
 * The conversion process:
 * - Converts ImageData objects to byte[] when persisting to the database.
 * - Converts byte[] back to ImageData when loading from the database.
 *
 * Null safety:
 * - If the entity attribute is null, the converter will ensure a null value is persisted to the database.
 * - If the database column contains a null value, the converter will return null when obtaining the entity attribute.
 */
@Converter
public class Base64ImageDataConverter implements AttributeConverter<ImageData, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(ImageData attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.bytes();
    }

    @Override
    public ImageData convertToEntityAttribute(byte[] dbData) {
        if (dbData == null) {
            return null;
        }
        return new ImageData(dbData);
    }
}
