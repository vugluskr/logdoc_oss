package ru.gang.logdoc.structs.dto;

import java.util.Objects;

public final class HiveId {
    public final String ip;
    public final int port;

    public HiveId(final String ip, final int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HiveId hiveId = (HiveId) o;
        return port == hiveId.port && ip.equals(hiveId.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
