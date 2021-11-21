package ru.gang.logdoc.structs;

import java.io.ByteArrayOutputStream;

public interface Recording {
    void endRecord(final ByteArrayOutputStream os);
    void startRecord(final ByteArrayOutputStream os);
}
