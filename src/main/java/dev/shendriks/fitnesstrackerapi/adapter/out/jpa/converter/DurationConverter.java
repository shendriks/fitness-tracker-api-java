package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter;

import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DurationConverter implements AttributeConverter<Duration, Double> {
    @Override
    public Double convertToDatabaseColumn(Duration attribute) {
        return attribute.toSeconds();
    }

    @Override
    public Duration convertToEntityAttribute(Double dbData) {
        return Duration.ofSeconds(dbData);
    }
}
