package ru.gang.logdoc.structs.utils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class DBAOutputStream extends ByteArrayOutputStream {
    public DBAOutputStream() {
    }

    public DBAOutputStream(final int size) {
        super(size);
    }

    public int asInt() {
        return Tools.asInt(buf);
    }

    public long asLong() {
        return Tools.getLong(buf);
    }

    public String asString() {
        return new String(toByteArray(), StandardCharsets.UTF_8);
    }
}
