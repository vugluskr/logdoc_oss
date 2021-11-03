package ru.gang.logdoc.structs.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static ru.gang.logdoc.structs.utils.StreamTools.NumberWriters.writeInt;
import static ru.gang.logdoc.structs.utils.StreamTools.NumberWriters.writeShort;

/**
 * @author Denis Danilin | denis@danilin.name
 * 18.06.2021 16:00
 * netty-backend ☭ sweat and blood
 */
public interface StreamTools {
    class NumberWriters {
        static void writeShort(final short sh, final OutputStream os) throws IOException {
            os.write((sh >>> 8) & 0xff);
            os.write((sh) & 0xff);
        }

        static void writeInt(final int in, final OutputStream os) throws IOException {
            os.write((in >>> 24) & 0xff);
            os.write((in >>> 16) & 0xff);
            os.write((in >>> 8) & 0xff);
            os.write((in) & 0xff);
        }

        static void writeLong(final long in, final OutputStream os) throws IOException {
            os.write((byte) (in >>> 56));
            os.write((byte) (in >>> 48));
            os.write((byte) (in >>> 40));
            os.write((byte) (in >>> 32));
            os.write((byte) (in >>> 24));
            os.write((byte) (in >>> 16));
            os.write((byte) (in >>> 8));
            os.write((byte) (in));
        }
    }

    class Writers {
        static void writeUtf(final String s, final OutputStream os) throws IOException {
            final byte[] data = Tools.notNull(s).getBytes(StandardCharsets.UTF_8);

            writeShort((short) data.length, os);
            os.write(data);
        }
    }

    class IntReader implements Consumer<Byte> {
        private final Consumer<Integer> consumer;
        private final byte[] buf;
        private final AtomicInteger i;

        public IntReader(final Consumer<Integer> consumer) {
            this.consumer = consumer;
            buf = new byte[4];
            i = new AtomicInteger(0);
        }

        @Override
        public void accept(final Byte b) {
            buf[i.getAndIncrement()] = b;
            if (i.get() == 4)
                consumer.accept(Tools.asInt(buf));
        }

        public void reset() {
            i.set(0);
        }
    }

    class LongReader implements Consumer<Byte> {
        private final Consumer<Long> consumer;
        private final byte[] buf;
        private final AtomicInteger i;

        public LongReader(final Consumer<Long> consumer) {
            this.consumer = consumer;
            buf = new byte[4];
            i = new AtomicInteger(0);
        }

        @Override
        public void accept(final Byte b) {
            buf[i.getAndIncrement()] = b;
            if (i.get() == 8)
                consumer.accept(Tools.asLong(buf));
        }

        public void reset() {
            i.set(0);
        }
    }

    class UtfReader implements Consumer<Byte> {
        private Consumer<Byte> subStrategy;

        public UtfReader(final Consumer<String> consumer) {
            subStrategy = new ShortReader(len -> subStrategy = new UtfSizedReader(len, consumer));
        }

        @Override
        public void accept(final Byte b) {
            subStrategy.accept(b);
        }
    }

    class ShortReader implements Consumer<Byte> {
        private final Consumer<Short> consumer;
        private final byte[] buf;
        private boolean f;

        public ShortReader(final Consumer<Short> consumer) {
            this.consumer = consumer;
            buf = new byte[2];
            f = true;
        }

        @Override
        public void accept(final Byte b) {
            buf[f ? 0 : 1] = b;
            if (!f) {
                consumer.accept(Tools.asShort(buf));
            }
            f = false;
        }

        public void reset() {
            f = true;
        }
    }

    class UtfSizedReader implements Consumer<Byte> {
        private final Consumer<String> consumer;
        private final byte[] buf;
        private int idx = 0;

        public UtfSizedReader(final int length, final Consumer<String> consumer) {
            this.buf = new byte[length];
            this.consumer = consumer;
            if (length <= 0)
                consumer.accept("");
        }

        @Override
        public void accept(final Byte b) {
            buf[idx++] = b;
            if (idx >= buf.length)
                consumer.accept(new String(buf, StandardCharsets.UTF_8));
        }
    }
}
