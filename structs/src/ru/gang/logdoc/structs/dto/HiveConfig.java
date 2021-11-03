package ru.gang.logdoc.structs.dto;

import java.util.ArrayList;
import java.util.Objects;

public final class HiveConfig extends ArrayList<SinkPort> {
    public HiveId id;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final HiveConfig sinkPorts = (HiveConfig) o;

        if (!id.equals(sinkPorts.id) || size() != ((HiveConfig) o).size())
            return false;

        for (final SinkPort port : this)
            if (!((HiveConfig) o).contains(port))
                return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
