package ru.gang.logdoc.structs.utils;

import ru.gang.logdoc.structs.Recording;
import ru.gang.logdoc.structs.query.Condition;
import ru.gang.logdoc.structs.query.Query;
import ru.gang.logdoc.structs.query.conditions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static ru.gang.logdoc.structs.utils.StreamTools.writeShort;
import static ru.gang.logdoc.structs.utils.StreamTools.writeUtf;
import static ru.gang.logdoc.structs.utils.Tools.logTimeFormat;

public class QueryWriteStrategy implements Function<Query, byte[]>, BiConsumer<Query, OutputStream>, Recording {
    @Override
    public byte[] apply(final Query query) {
        try {
            return write(query);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void accept(final Query query, final OutputStream os) {
        try {
            os.write(write(query));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] write(final Query query) throws IOException {
        try (final ByteArrayOutputStream os = new ByteArrayOutputStream(4096)) {
            os.write(query.join.ordinal());
            writeShort((short) query.size(), os);

            for (final Condition c : query) {
                if (c instanceof Query) {
                    os.write(0);
                    os.write(write((Query) c));
                } else if (c instanceof TimeMatch) {
                    os.write(1);
                    writeTimeMatch((TimeMatch) c, os);
                } else if (c instanceof SourceMatch) {
                    os.write(2);
                    writeSourceMatch((SourceMatch) c, os);
                } else if (c instanceof Grep) {
                    os.write(3);
                    writeGrep((Grep) c, os);
                } else if (c instanceof FieldMatch) {
                    os.write(4);
                    writeFieldMatch((FieldMatch) c, os);
                } else if (c instanceof LevelMatch) {
                    os.write(5);
                    writeLevelMatch((LevelMatch) c, os);
                } else if (c instanceof SourceIpMatch) {
                    os.write(6);
                    writeSourceIpMatch((SourceIpMatch) c, os);
                } else if (c instanceof SourceIdMatch) {
                    os.write(7);
                    writeSourceIdMatch((SourceIdMatch) c, os);
                }
            }

            return os.toByteArray();
        }
    }

    @Override
    public void endRecord(final ByteArrayOutputStream os) {
    }

    @Override
    public void startRecord(final ByteArrayOutputStream os) {
    }

    private void writeTimeMatch(final TimeMatch match, final OutputStream os) throws IOException {
        os.write(match.srcTime ? 1 : 0);
        writeUtf(match.since == null ? "" : match.since.format(logTimeFormat), os);
        writeUtf(match.till == null ? "" : match.till.format(logTimeFormat), os);
        os.write(match.sinceOp.ordinal());
        os.write(match.tillOp.ordinal());
    }

    private void writeSourceMatch(final SourceMatch match, final OutputStream os) throws IOException {
        os.write(match.match.ordinal());
        writeUtf(match.sample, os);
    }

    private void writeSourceIpMatch(final SourceIpMatch match, final OutputStream os) throws IOException {
        os.write(match.match.ordinal());
        writeUtf(match.sample, os);
    }

    private void writeSourceIdMatch(final SourceIdMatch match, final OutputStream os) throws IOException {
        os.write(match.match.ordinal());
        writeUtf(match.sample, os);
    }

    private void writeGrep(final Grep grep, final OutputStream os) throws IOException {
        writeUtf(grep.sample, os);
        os.write(grep.caseSensitive ? 1 : 0);
    }

    private void writeFieldMatch(final FieldMatch match, final OutputStream os) throws IOException {
        writeUtf(match.field, os);
        os.write(match.match.ordinal());
        os.write(match.strict ? 1 : 0);
        os.write(match.missFieldOk ? 1 : 0);
        writeUtf(match.sample, os);
    }

    private void writeLevelMatch(final LevelMatch match, final OutputStream os) throws IOException {
        os.write(match.coOp.ordinal());
        os.write(match.sample.ordinal());
    }
}
