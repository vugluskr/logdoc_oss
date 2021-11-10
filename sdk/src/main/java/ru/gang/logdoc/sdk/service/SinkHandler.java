package ru.gang.logdoc.sdk.service;

import com.typesafe.config.Config;
import ru.gang.logdoc.sdk.ConnectionType;
import ru.gang.logdoc.sdk.SinkId;

import java.net.SocketAddress;
import java.util.Collection;

public interface SinkHandler {
    void configure(Config config);

    void handle(byte[] data, SocketAddress from, SinkId id);

    Collection<ConnectionType> sinkTypes();
}
