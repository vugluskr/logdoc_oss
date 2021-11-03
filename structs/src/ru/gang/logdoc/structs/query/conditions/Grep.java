package ru.gang.logdoc.structs.query.conditions;

import ru.gang.logdoc.LogDoc;
import ru.gang.logdoc.structs.query.Condition;

import java.util.Map;
import java.util.Objects;

import static ru.gang.logdoc.structs.utils.Tools.isEmpty;

public class Grep implements Condition {
    public final boolean caseSensitive;
    public final String sample;

    public Grep(final String sample) {
        this(sample, true);
    }

    public Grep(final String sample, final boolean caseSensitive) {
        if (isEmpty(sample))
            throw new IllegalArgumentException();

        this.caseSensitive = caseSensitive;
        this.sample = caseSensitive ? sample : sample.toLowerCase();
    }

    @Override
    public boolean match(final Map<String, String> entry) {
        if (caseSensitive)
            return entry.get(LogDoc.FieldMessage).contains(sample);

        return entry.get(LogDoc.FieldMessage).toLowerCase().contains(sample);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() || !super.equals(o)) return false;
        final Grep grep = (Grep) o;
        return caseSensitive == grep.caseSensitive && sample.equals(grep.sample);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseSensitive, sample);
    }
}
