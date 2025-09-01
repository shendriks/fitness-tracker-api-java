package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter;

import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DistanceConverter implements AttributeConverter<Distance, Double> {
    @Override
    public Double convertToDatabaseColumn(Distance attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toMeters();
    }

    @Override
    public Distance convertToEntityAttribute(Double dbData) {
        if (dbData == null) {
            return null;
        }
        return Distance.ofMeters(dbData);
    }
}
