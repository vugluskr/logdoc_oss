package ru.gang.logdoc.structs.query.conditions;

import ru.gang.logdoc.structs.enums.Match;
import ru.gang.logdoc.structs.query.Condition;

import java.util.Map;
import java.util.Objects;

import static ru.gang.logdoc.structs.utils.Tools.isEmpty;


public class FieldMatch extends AMatcher implements Condition {
    public final String field;
    public final boolean strict, missFieldOk;

    public FieldMatch(final String field, final Match match, final String sample) {
        this(field, match, true, sample);
    }

    public FieldMatch(final String field, final Match match, final boolean strict, final String sample) {
        this(field, match, strict, true, sample);
    }

    public FieldMatch(final String field, final Match match, final boolean strict, final boolean missFieldOk, final String sample) {
        super(sample, match);
        if (isEmpty(sample))
            throw new IllegalArgumentException();

        this.field = field;
        this.strict = strict;
        this.missFieldOk = missFieldOk;
    }

    @Override
    public boolean match(final Map<String, String> entry) {
        return (missFieldOk && !entry.containsKey(field)) || doesMatch(entry.get(field));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() || !super.equals(o)) return false;
        final FieldMatch that = (FieldMatch) o;
        return strict == that.strict && missFieldOk == that.missFieldOk && field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, strict, missFieldOk);
    }
}
