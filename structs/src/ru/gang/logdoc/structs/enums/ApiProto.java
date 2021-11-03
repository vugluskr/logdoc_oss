package ru.gang.logdoc.structs.enums;

import ru.gang.logdoc.structs.utils.Tools;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author Denis Danilin | denis@danilin.name
 * 18.06.2021 12:38
 * netty-backend ☭ sweat and blood
 */
public enum ApiProto {
    Void,
    SetListener,
    SetWatcher,
    RemoveListener,
    RemoveWatcher,
    FireListener,
    FireWatcher,
    GraceStop,
    SetupSink,
    GetHiveInfo;

    public static ApiProto is(byte b) {
        if (b < 0 || b >= values().length)
            return Void;

        return values()[b];
    }

    public static Consumer<Byte> resolver(final Consumer<ApiProto> consumer) {
        final AtomicInteger ctr = new AtomicInteger(0);

        return b -> {
            if (b == -1) {
                consumer.accept(ApiProto.GraceStop);
                return;
            }

            if (ctr.get() < Tools.header.length) {
                if (b != Tools.header[ctr.getAndIncrement()])
                    ctr.set(0);

                return;
            }

            ctr.set(0);
            consumer.accept(ApiProto.is(b));
        };
    }
}
