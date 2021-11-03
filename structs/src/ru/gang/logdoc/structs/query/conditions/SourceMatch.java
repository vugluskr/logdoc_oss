package ru.gang.logdoc.structs.query.conditions;

import ru.gang.logdoc.LogDoc;
import ru.gang.logdoc.structs.enums.Match;
import ru.gang.logdoc.structs.query.Condition;

import java.util.Map;

public class SourceMatch extends AMatcher implements Condition {
    public SourceMatch(final String sample) {
        this(sample, Match.Equals);
    }

    public SourceMatch(final String sample, final Match match) {
        super(sample, match);
    }

    @Override
    public boolean match(final Map<String, String> entry) {
        return doesMatch(entry.get(LogDoc.FieldSource));
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
