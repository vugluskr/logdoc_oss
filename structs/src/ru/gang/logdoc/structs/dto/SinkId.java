package ru.gang.logdoc.structs.dto;

import ru.gang.logdoc.structs.enums.Sink;

import java.util.Objects;

public final class SinkId {
    public final int port;
    public final Sink type;

    public SinkId(final int port, final Sink type) {
        this.port = port;
        this.type = type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SinkId sinkId = (SinkId) o;
        return port == sinkId.port && type == sinkId.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, type);
    }
}
