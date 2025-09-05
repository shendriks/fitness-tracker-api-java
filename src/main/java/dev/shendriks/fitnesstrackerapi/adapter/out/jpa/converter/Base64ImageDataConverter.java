package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter;

import dev.shendriks.fitnesstrackerapi.domain.value.Base64ImageData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class Base64ImageDataConverter implements AttributeConverter<Base64ImageData, String> {
    @Override
    public String convertToDatabaseColumn(Base64ImageData attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.value();
    }

    @Override
    public Base64ImageData convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new Base64ImageData(dbData);
    }
}
