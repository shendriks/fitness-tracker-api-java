package dev.shendriks.fitnesstrackerapi.supportive.ulid;

import com.github.f4b6a3.ulid.UlidCreator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;

import java.util.EnumSet;

public class UlidGenerator implements BeforeExecutionGenerator {
    @Override
    public Object generate(
        SharedSessionContractImplementor sharedSessionContractImplementor,
        Object o,
        Object o1,
        EventType eventType
    ) {
        return UlidCreator.getUlid().toString();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EventTypeSets.INSERT_ONLY;
    }
}