package ru.gang.logdoc.sdk;

import ru.gang.logdoc.sdk.enums.Proto;

import java.util.Objects;

public class ConnectionType implements Comparable<ConnectionType> {
    public final Proto proto;
    public final String name;

    public ConnectionType(final Proto proto, final String name) {
        if (proto == null)
            throw new NullPointerException("Type protocol is undefined");

        if (name == null || name.trim().isEmpty())
            throw new NullPointerException("Type name is undefined");

        this.proto = proto;
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ConnectionType that = (ConnectionType) o;
        return proto == that.proto && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proto, name);
    }

    @Override
    public int compareTo(final ConnectionType o) {
        final int res = name.compareTo(o.name);

        return res != 0 ? res : proto.name().compareTo(o.proto.name());
    }
}
