package ru.gang.logdoc.sdk;

import java.util.Objects;

public final class SinkId {

    public final int port;
    public final String name;
    public final ConnectionType type;

    public SinkId(final int port, final String name, final ConnectionType type) {
        this.port = port;
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SinkId sinkId = (SinkId) o;
        return port == sinkId.port && type.equals(sinkId.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, type);
    }

}
