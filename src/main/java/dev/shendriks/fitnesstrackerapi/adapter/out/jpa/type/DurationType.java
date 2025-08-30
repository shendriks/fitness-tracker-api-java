package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.type;

import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class DurationType implements UserType<Duration> {
    @Override
    public int getSqlType() {
        return Types.BIGINT;
    }

    @Override
    public Class<Duration> returnedClass() {
        return Duration.class;
    }

    @Override
    public boolean equals(Duration duration, Duration j1) {
        return duration.equals(j1);
    }

    @Override
    public int hashCode(Duration duration) {
        return duration.hashCode();
    }

    @Override
    public Duration nullSafeGet(ResultSet rs, int i, SharedSessionContractImplementor ssci, Object o) throws SQLException {
        long durationValue = rs.getLong(i);
        return Duration.ofSeconds(durationValue);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Duration duration, int i, SharedSessionContractImplementor ssci) throws SQLException {
        if (Objects.isNull(duration))
            st.setNull(i, Types.DOUBLE);
        else {
            long durationValue = duration.toSeconds();
            st.setLong(i, durationValue);
        }
    }

    @Override
    public Duration deepCopy(Duration duration) {
        return Duration.ofSeconds(duration.toSeconds());
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Duration duration) {
        return null;
    }

    @Override
    public Duration assemble(Serializable serializable, Object o) {
        return null;
    }
}
