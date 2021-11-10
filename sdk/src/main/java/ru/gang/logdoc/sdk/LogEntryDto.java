package ru.gang.logdoc.sdk;


import java.util.Map;

/**
 * @author Denis Danilin | denis@danilin.name
 * 27.02.2021 13:44
 * logdoc ☭ sweat and blood
 */
public class LogEntryDto {
    public String srcTime;
    public String rcvTime;
    public String ip;
    public String id;
    public String source;
    public String level;
    public String entry;
    public Map<String, String> fields;
}
