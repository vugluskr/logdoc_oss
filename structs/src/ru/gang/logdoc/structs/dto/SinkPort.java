package ru.gang.logdoc.structs.dto;

import java.util.Arrays;
import java.util.Objects;

public class SinkPort {
    public SinkId id;
    public byte[] delimiters;
    public String locale;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SinkPort port = (SinkPort) o;
        return id.equals(port.id) && Arrays.equals(delimiters, port.delimiters) && Objects.equals(locale, port.locale);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, locale);
        result = 31 * result + Arrays.hashCode(delimiters);
        return result;
    }
}
