package ru.gang.logdoc.structs.utils;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class Tools {
    public static final DateTimeFormatter logTimeFormat = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");
    public static final byte[] header = new byte[] {(byte) 6, (byte) 3};
    public static final SecureRandom rnd;

    static {
        rnd = new SecureRandom();
        rnd.setSeed(System.currentTimeMillis());
    }

    public static long asLong(final byte[] buf) {
        if (isEmpty(buf) || buf.length < 8)
            return 0;

        return ((long) buf[0] << 56) +
                ((long) (buf[1] & 255) << 48) +
                ((long) (buf[2] & 255) << 40) +
                ((long) (buf[3] & 255) << 32) +
                ((long) (buf[4] & 255) << 24) +
                ((buf[5] & 255) << 16) +
                ((buf[6] & 255) << 8) +
                ((buf[7] & 255));
    }

    public static int asInt(final byte[] buf) {
        if (isEmpty(buf) || buf.length < 4)
            return 0;

        return ((buf[0] & 0xff) << 24) + ((buf[1] & 0xff) << 16) + ((buf[2] & 0xff) << 8) + (buf[3] & 0xff);
    }

    public static short asShort(final byte[] buf) {
        if (isEmpty(buf) || buf.length < 2)
            return 0;

        return (short) ((buf[0] & 0xFF) << 8 | (buf[1] & 0xFF));
    }

    public static int getInt(final Object parameter) {
        final String param = notNull(parameter);
        try {
            return Integer.decode(param);
        } catch (Exception e) {
            try {
                return Integer.parseInt(param.replaceAll("([^0-9-])", ""));
            } catch (Exception ee) {
                return 0;
            }
        }
    }

    public static long getLong(final Object parameter) {
        final String param = notNull(parameter);
        try {
            return Long.decode(param);
        } catch (Exception e) {
            try {
                return Long.parseLong(param.replaceAll("([^0-9-])", ""));
            } catch (Exception ee) {
                return 0;
            }
        }
    }

    public static String notNull(final Object o, final String def) {
        if (o == null)
            return def == null ? "" : def.trim();

        if (o instanceof String)
            return ((String) o).trim();

        return String.valueOf(o).trim();
    }

    public static String notNull(final Object o) {
        return notNull(o, "");
    }

    public static boolean isEmpty(final Object o) {
        if (o == null)
            return true;

        if (o.getClass().isArray())
            return Array.getLength(o) == 0;

        if (o instanceof Collection)
            return ((Collection) o).isEmpty();

        if (o instanceof Map)
            return ((Map) o).isEmpty();

        if (o.getClass().isEnum())
            return false;

        return notNull(o).isEmpty();
    }

    public static String tokenString() {
        return generateUuid().toString();
    }

    public static byte[] token() {
        return asBytes(generateUuid());
    }

    public static String tokenOf(final byte[] array) {
        final ByteBuffer bb = ByteBuffer.wrap(array);

        return new UUID(bb.getLong(), bb.getLong()).toString();
    }

    public static byte[] tokenOf(final String token) {
        return asBytes(UUID.fromString(token));
    }

    private static byte[] asBytes(final UUID uuid) {
        final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);

        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }

    private static long tol(final byte[] buf, final int shift) {
        return (toi(buf, shift) << 32) + ((toi(buf, shift + 4) << 32) >>> 32);
    }

    private static long toi(final byte[] buf, int shift) {
        return (buf[shift] << 24)
                + ((buf[++shift] & 0xFF) << 16)
                + ((buf[++shift] & 0xFF) << 8)
                + (buf[++shift] & 0xFF);
    }

    public static UUID generateUuid() {
        final byte[] buffer = new byte[16];
        rnd.nextBytes(buffer);

        return generateUuid(buffer);
    }

    private static UUID generateUuid(final byte[] buffer) {
        long r1, r2;

        r1 = tol(buffer, 0);
        r2 = tol(buffer, 1);

        r1 &= ~0xF000L;
        r1 |= 4 << 12;
        r2 = ((r2 << 2) >>> 2);
        r2 |= (2L << 62);

        return new UUID(r1, r2);
    }
}
