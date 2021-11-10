package ru.gang.logdoc.structs.query.conditions;

import ru.gang.logdoc.sdk.LogDoc;
import ru.gang.logdoc.structs.enums.Match;
import ru.gang.logdoc.structs.query.Condition;

import java.util.Map;

/**
 * @author Denis Danilin | denis@danilin.name
 * 28.07.2021 11:40
 * logdoc-structs ☭ sweat and blood
 */
public class SourceIpMatch extends AMatcher implements Condition {
    public SourceIpMatch(final String sample) {
        this(sample, Match.Equals);
    }

    public SourceIpMatch(final String sample, final Match match) {
        super(sample, match);
    }

    @Override
    public boolean match(final Map<String, String> entry) {
        return doesMatch(entry.get(LogDoc.FieldIp));
    }

    @Override
    public boolean equals(final Object o) {
        return o != null && getClass() == o.getClass() && super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
