package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter;

import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DistanceConverter implements AttributeConverter<Distance, Double> {
    @Override
    public Double convertToDatabaseColumn(Distance attribute) {
        return attribute.toMeters();
    }

    @Override
    public Distance convertToEntityAttribute(Double dbData) {
        return Distance.ofMeters(dbData);
    }
}
