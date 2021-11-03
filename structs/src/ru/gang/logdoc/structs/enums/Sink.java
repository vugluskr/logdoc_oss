package ru.gang.logdoc.structs.enums;

public enum Sink {
    APP_TCP(Proto.TCP), APP_UDP(Proto.UDP), APP_HTTP(Proto.HTTP), SYS_TCP(Proto.TCP), SYS_UDP(Proto.UDP), JRN_UDP(Proto.HTTP), API(Proto.TCP);

    public final Proto proto;

    Sink(final Proto proto) {this.proto = proto;}

    public enum Proto {
        TCP, UDP, HTTP
    }
}
