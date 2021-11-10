package ru.gang.logdoc.sdk;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface LogDoc {
    String FieldListenerId = "lsn";
    String FieldTimeStamp = "time_src";
    String FieldProcessId = "source_id";
    String FieldSource = "source_name";
    String FieldLevel = "level";
    String FieldMessage = "log_message";
    String FieldTimeRcv = "time_rcv";
    String FieldIp = "source_ip";

    Set<String> controls = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(FieldTimeStamp, FieldProcessId, FieldSource, FieldLevel, FieldMessage, FieldTimeRcv, FieldIp,
            FieldListenerId)));
}
