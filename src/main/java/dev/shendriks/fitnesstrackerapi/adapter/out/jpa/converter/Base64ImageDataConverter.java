package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter;

import dev.shendriks.fitnesstrackerapi.domain.value.ImageData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
