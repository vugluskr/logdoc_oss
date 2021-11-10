package ru.gang.logdoc.structs.utils;

import ru.gang.logdoc.sdk.ConnectionType;
import ru.gang.logdoc.sdk.SinkId;
import ru.gang.logdoc.sdk.enums.Proto;
import ru.gang.logdoc.structs.dto.HiveConfig;
import ru.gang.logdoc.structs.dto.HiveId;

import java.io.EOFException;
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
        final Consumer<SinkId> finisher = sinkPort -> {
            config.add(sinkPort);
            if (config.size() == max)
                successConsumer.accept(config);
            else
                strategy = readChild(max);
        };

        return new StreamTools.UtfReader(name ->
                strategy = protoByte ->
                        strategy = new StreamTools.UtfReader(typeName ->
                                strategy = new StreamTools.IntReader(port ->
                                        finisher.accept(new SinkId(port, name, new ConnectionType(Proto.values()[protoByte], typeName))))));
    }
}
