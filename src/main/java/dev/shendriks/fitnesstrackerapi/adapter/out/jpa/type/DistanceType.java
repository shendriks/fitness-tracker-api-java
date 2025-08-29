package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.type;

import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class DistanceType implements UserType<Distance> {
    @Override
    public int getSqlType() {
        return Types.DOUBLE;
    }

    @Override
    public Class<Distance> returnedClass() {
        return Distance.class;
    }

    @Override
    public boolean equals(Distance distance, Distance other) {
        return Objects.equals(distance, other);
    }

    @Override
    public int hashCode(Distance distance) {
        return distance.hashCode();
    }

    @Override
    public Distance nullSafeGet(ResultSet rs, int i, SharedSessionContractImplementor ssci, Object o) throws SQLException {
        double distanceValue = rs.getDouble(i);
        return Distance.ofMeters(distanceValue);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Distance distance, int i, SharedSessionContractImplementor ssci) throws SQLException {
        if (Objects.isNull(distance))
            st.setNull(i, Types.DOUBLE);
        else {
            double distanceValue = distance.toMeters();
            st.setDouble(i, distanceValue);
        }
    }

    @Override
    public Distance deepCopy(Distance distance) {
        return Distance.ofMeters(distance.toMeters());
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Distance value) {
        return null;
    }

    @Override
    public Distance assemble(Serializable cached, Object owner) {
        return null;
    }
}
