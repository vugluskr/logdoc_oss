package ru.gang.logdoc.structs.query;


import java.util.Map;

public interface Condition {

    boolean match(Map<String, String> entry);

}
