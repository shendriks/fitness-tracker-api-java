package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.converter;

import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DurationConverter implements AttributeConverter<Duration, Long> {
    @Override
    public Long convertToDatabaseColumn(Duration attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toSeconds();
    }

    @Override
    public Duration convertToEntityAttribute(Long dbData) {
        if (dbData == null) {
            return null;
        }
        return Duration.ofSeconds(dbData);
    }
}
