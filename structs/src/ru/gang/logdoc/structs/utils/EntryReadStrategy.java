package ru.gang.logdoc.structs.utils;

import ru.gang.logdoc.LogDoc;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ru.gang.logdoc.structs.utils.Tools.isEmpty;

public class EntryReadStrategy implements Consumer<Byte> {
    private enum STAGE {
        gap, fieldName, valueSize, valueOneLine, valueSized
    }

    protected final Consumer<Byte> strategy;
    private final Map<String, String> fieldMap;
    private final Consumer<Map<String, String>> successConsumer;

    private String currentField;
    private long currentSize;
    private DBAOutputStream buf;
    protected STAGE stage;

    public EntryReadStrategy(final Consumer<Map<String, String>> successConsumer, final BiConsumer<Throwable, Map<String, String>> errorConsumer) {
        this.successConsumer = successConsumer;
        fieldMap = new HashMap<>(0);
        reset();

        /*
        simple_field_name=one line field value\n
        complex_field_name\n
        25 00 00 00 00 00 00 00complex\n
        field\n
        value
        * */

        strategy = b -> {
            if (b == -1) {
                errorConsumer.accept(new EOFException(), fieldMap);
                return;
            }

            try {
                switch (stage) {
                    case fieldName:
                        if (b == '\n' || b == '=') {
                            submitFieldName();

                            stage = b == '=' ? STAGE.valueOneLine : STAGE.valueSize;
                        } else {
                            if (fieldMap.isEmpty())
                                fieldMap.put(LogDoc.FieldTimeRcv, LocalDateTime.now().format(Tools.logTimeFormat));
                            buf.write(b);
                        }
                        break;
                    case gap:
                        if (b == '\n')
                            submitPacket();
                        else {
                            stage = STAGE.fieldName;
                            buf.write(b);
                        }
                        break;
                    case valueOneLine:
                        if (b == '\n')
                            submitField();
                        else
                            buf.write(b);
                        break;
                    case valueSize:
                        buf.write(b);
                        if (buf.size() == 4)
                            submitSize();
                        break;
                    case valueSized:
                        buf.write(b);
                        if (buf.size() == currentSize)
                            submitField();
                        break;
                }
            } catch (final Exception e) {
                reset();
                errorConsumer.accept(e, fieldMap);
            }
        };
    }

    private void submitSize() {
        currentSize = buf.asInt();
        initBuf();
        stage = STAGE.valueSized;
    }

    private void submitFieldName() {
        currentField = buf.asString();
        initBuf();
    }

    private void submitField() {
        fieldMap.put(currentField, buf.asString());
        initBuf();
        stage = STAGE.gap;
        currentSize = 0;
    }

    private void submitPacket() {
        if (isEmpty(fieldMap))
            return;

        successConsumer.accept(fieldMap);
        reset();
    }

    public void reset() {
        initBuf();
        stage = STAGE.fieldName;
        fieldMap.clear();
    }

    private void initBuf() {
        buf = new DBAOutputStream(4096);
    }

    @Override
    public void accept(final Byte b) {
        strategy.accept(b);
    }
}
