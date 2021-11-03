package ru.gang.logdoc.structs.utils;

import ru.gang.logdoc.structs.dto.HiveConfig;
import ru.gang.logdoc.structs.dto.HiveId;
import ru.gang.logdoc.structs.dto.SinkId;
import ru.gang.logdoc.structs.dto.SinkPort;
import ru.gang.logdoc.structs.enums.Sink;

import java.io.EOFException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class HiveSetupReadStrategy implements Consumer<Byte> {
    protected Consumer<Byte> strategy;
    private final HiveConfig config;
    private final Consumer<HiveConfig> successConsumer;
    private final Consumer<Throwable> errorConsumer;


    public HiveSetupReadStrategy(final Consumer<HiveConfig> successConsumer, final Consumer<Throwable> errorConsumer) {
        this.successConsumer = successConsumer;
        this.errorConsumer = errorConsumer;

        config = new HiveConfig();

        strategy = new StreamTools.IntReader(port ->
                strategy = new StreamTools.UtfReader(ip -> {
                    config.id = new HiveId(ip, port);

                    strategy = new StreamTools.ShortReader(size ->
                            strategy = readChild(size));
                }));
    }

    @Override
    public void accept(final Byte b) {
        if (b == -1) {
            errorConsumer.accept(new EOFException());
            return;
        }

        strategy.accept(b);
    }

    private Consumer<Byte> readChild(final int max) {
        final SinkPort sinkPort = new SinkPort();
        final Consumer<Void> finisher = unused -> {
            config.add(sinkPort);
            if (config.size() == max)
                successConsumer.accept(config);
            else
                strategy = readChild(max);
        };

        return type ->
                strategy = new StreamTools.IntReader(port2 -> {
                    sinkPort.id = new SinkId(port2, Sink.values()[type]);

                    if (sinkPort.id.type == Sink.SYS_TCP || sinkPort.id.type == Sink.SYS_UDP) {
                        strategy = new StreamTools.UtfReader(locale -> {
                            sinkPort.locale = locale;

                            if (sinkPort.id.type == Sink.SYS_TCP)
                                strategy = new StreamTools.ShortReader(dSize -> {
                                    sinkPort.delimiters = new byte[dSize];
                                    final AtomicInteger idx = new AtomicInteger(0);

                                    strategy = b3 -> {
                                        sinkPort.delimiters[idx.getAndIncrement()] = b3;

                                        if (idx.get() == dSize)
                                            finisher.accept(null);
                                    };
                                });
                            else
                                finisher.accept(null);
                        });
                    } else
                        finisher.accept(null);
                });
    }
}
