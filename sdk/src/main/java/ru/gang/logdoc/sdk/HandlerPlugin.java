package ru.gang.logdoc.sdk;


import com.google.inject.Inject;
import com.typesafe.config.Config;
import ru.gang.logdoc.sdk.service.LogdocCore;
import ru.gang.logdoc.sdk.service.SinkHandler;

import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class HandlerPlugin implements SinkHandler {
    public static final DateTimeFormatter logTimeFormat = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");

    @Inject
    private LogdocCore core;

    @Override
    public final void handle(final byte[] data, final SocketAddress from, final SinkId id) {
        final LogEntryDto dto = chunk(data, from, id);

        if (dto == null || isEmpty(dto.entry) || isEmpty(dto.source) || isEmpty(dto.level))
            return;

        if (isEmpty(dto.srcTime))
            dto.srcTime = LocalDateTime.now().format(logTimeFormat);
        if (isEmpty(dto.rcvTime))
            dto.rcvTime = LocalDateTime.now().format(logTimeFormat);
        if (isEmpty(dto.ip))
            dto.ip = ((InetSocketAddress) from).getHostName();

        core.handle(asMap(dto));
    }

    @Override
    public void configure(final Config config) {
    }

    protected abstract LogEntryDto chunk(final byte[] data, final SocketAddress from, final SinkId id);

    private Map<String, String> asMap(final LogEntryDto dto) {
        final Map<String, String> map = new HashMap<>(10);
        if (!isEmpty(dto.fields))
            map.putAll(dto.fields);

        map.put(LogDoc.FieldIp, dto.ip);
        map.put(LogDoc.FieldLevel, dto.level);
        map.put(LogDoc.FieldMessage, dto.entry);
        map.put(LogDoc.FieldProcessId, dto.id);
        map.put(LogDoc.FieldSource, dto.source);
        map.put(LogDoc.FieldTimeRcv, dto.rcvTime);
        map.put(LogDoc.FieldTimeStamp, dto.srcTime);

        return map;
    }

    public boolean isEmpty(final Object o) {
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
}
