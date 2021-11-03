package ru.gang.logdoc.structs.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

public class EntryWriteStrategy implements Function<Map<String, String>, byte[]> {
    @Override
    public byte[] apply(final Map<String, String> map) {
        try {
            return writeMap(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] writeMap(final Map<String, String> map) throws IOException {
        if (map == null)
            throw new NullPointerException();

        if (map.isEmpty())
            return new byte[0];

        try (final ByteArrayOutputStream os = new ByteArrayOutputStream(4096)) {
            startRecord(os);
            for (final Map.Entry<String, String> e : map.entrySet())
                writePair(e.getKey(), e.getValue(), os);
            endRecord(os);
            return os.toByteArray();
        }
    }

    public void endRecord(final ByteArrayOutputStream os) {
    }

    public void startRecord(final ByteArrayOutputStream os) {
    }

    private void writePair(final String key, final String value, final OutputStream daos) throws IOException {
        if (value == null)
            return;

        if (value.indexOf('\n') != -1)
            writeComplexPair(key, value, daos);
        else
            writeSimplePart(key, value, daos);
    }

    private void writeComplexPair(final String key, final String value, final OutputStream daos) throws IOException {
        final byte[] v = value.getBytes(StandardCharsets.UTF_8);
        daos.write(key.getBytes(StandardCharsets.UTF_8));
        daos.write('\n');
        final int l = v.length;
        daos.write((l >>> 24) & 0xff);
        daos.write((l >>> 16) & 0xff);
        daos.write((l >>> 8) & 0xff);
        daos.write((l) & 0xff);
        daos.write(v);
    }

    private void writeSimplePart(final String key, final String value, final OutputStream daos) throws IOException {
        daos.write((key + "=" + value + "\n").getBytes(StandardCharsets.UTF_8));
    }

}
