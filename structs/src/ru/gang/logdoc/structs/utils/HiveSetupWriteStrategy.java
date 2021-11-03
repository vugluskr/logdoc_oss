package ru.gang.logdoc.structs.utils;

import ru.gang.logdoc.structs.dto.HiveConfig;
import ru.gang.logdoc.structs.dto.SinkPort;
import ru.gang.logdoc.structs.enums.Sink;

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

            for (final SinkPort port : config) {
                os.write(port.id.type.ordinal());
                writeInt(port.id.port, os);

                if (port.id.type == Sink.SYS_TCP || port.id.type == Sink.SYS_UDP) {
                    writeUtf(port.locale, os);

                    if (port.id.type == Sink.SYS_TCP) {
                        os.write(port.delimiters != null ? port.delimiters.length : 0);
                        if (port.delimiters != null)
                            os.write(port.delimiters);
                    }
                }
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
