package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter;

import dev.shendriks.fitnesstrackerapi.domain.value.Speed;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SpeedConverter implements AttributeConverter<Speed, Double> {
    @Override
    public Double convertToDatabaseColumn(Speed attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toMetersPerSecond();
    }

    @Override
    public Speed convertToEntityAttribute(Double dbData) {
        if (dbData == null) {
            return null;
        }
        return Speed.ofMetersPerSecond(dbData);
    }
}
