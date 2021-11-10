package ru.gang.logdoc.structs.utils;

import ru.gang.logdoc.sdk.SinkId;
import ru.gang.logdoc.structs.dto.HiveConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

import static ru.gang.logdoc.structs.utils.StreamTools.NumberWriters.writeInt;
import static ru.gang.logdoc.structs.utils.StreamTools.NumberWriters.writeShort;
import static ru.gang.logdoc.structs.utils.StreamTools.Writers.writeUtf;

public class HiveSetupWriteStrategy implements Function<HiveConfig, byte[]> {
    @Override
    public byte[] apply(final HiveConfig config) {
        try {
            return writeData(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] writeData(final HiveConfig config) throws IOException {
        if (config == null)
            throw new NullPointerException();

        try (final ByteArrayOutputStream os = new ByteArrayOutputStream(4096)) {
            startRecord(os);
            writeInt(config.id.port, os);
            writeUtf(config.id.ip, os);
            writeShort((short) config.size(), os);

            for (final SinkId port : config) {
                writeUtf(port.name, os);
                os.write(port.type.proto.ordinal());
                writeUtf(port.type.name, os);
                writeInt(port.port, os);
            }

            endRecord(os);
            return os.toByteArray();
        }
    }

    public void endRecord(final ByteArrayOutputStream os) {
    }

    public void startRecord(final ByteArrayOutputStream os) {
    }
}
