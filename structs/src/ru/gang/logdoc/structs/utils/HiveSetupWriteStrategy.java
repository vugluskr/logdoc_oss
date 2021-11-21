package ru.gang.logdoc.structs.utils;

import ru.gang.logdoc.sdk.SinkId;
import ru.gang.logdoc.structs.Recording;
import ru.gang.logdoc.structs.dto.HiveConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static ru.gang.logdoc.structs.utils.StreamTools.*;

public class HiveSetupWriteStrategy implements Function<HiveConfig, byte[]>, BiConsumer<HiveConfig, OutputStream>, Recording {
    @Override
    public void accept(final HiveConfig sinkIds, final OutputStream os) {
        try {
            os.write(writeData(sinkIds));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

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
