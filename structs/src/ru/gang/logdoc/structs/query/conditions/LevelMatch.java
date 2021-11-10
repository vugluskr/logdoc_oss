package ru.gang.logdoc.structs.query.conditions;

import ru.gang.logdoc.sdk.LogDoc;
import ru.gang.logdoc.structs.enums.CoOp;
import ru.gang.logdoc.structs.enums.LogLevel;
import ru.gang.logdoc.structs.query.Condition;

import java.util.Map;
import java.util.Objects;

public class LevelMatch implements Condition {

    public final CoOp coOp;
    public final LogLevel sample;

    public LevelMatch(final CoOp coOp, final LogLevel sample) {
        this.coOp = coOp;
        this.sample = sample;
    }

    @Override
    public boolean match(final Map<String, String> entry) {
        switch (coOp) {
            case equal: return sample.name().equals(entry.get(LogDoc.FieldLevel));
            case notEqual: return !sample.name().equals(entry.get(LogDoc.FieldLevel));
            case greater: return LogLevel.valueOf(entry.get(LogDoc.FieldLevel)).ordinal() > sample.ordinal();
            case lower: return LogLevel.valueOf(entry.get(LogDoc.FieldLevel)).ordinal() < sample.ordinal();
            case greaterEqual: return LogLevel.valueOf(entry.get(LogDoc.FieldLevel)).ordinal() >= sample.ordinal();
            case lowerEqual: return LogLevel.valueOf(entry.get(LogDoc.FieldLevel)).ordinal() <= sample.ordinal();
        };

        return false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() || !super.equals(o)) return false;
        final LevelMatch that = (LevelMatch) o;
        return coOp == that.coOp && sample == that.sample;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coOp, sample);
    }
}
