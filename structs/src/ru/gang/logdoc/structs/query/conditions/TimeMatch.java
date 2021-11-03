package ru.gang.logdoc.structs.query.conditions;


import ru.gang.logdoc.LogDoc;
import ru.gang.logdoc.structs.enums.CoOp;
import ru.gang.logdoc.structs.query.Condition;
import ru.gang.logdoc.structs.utils.Tools;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * @author Denis Danilin | denis@danilin.name
 * 05.01.2021 13:18
 * logdoc ☭ sweat and blood
 */
public class TimeMatch implements Condition {
    public final boolean srcTime;
    public final LocalDateTime since, till;
    public final CoOp sinceOp, tillOp;

    public TimeMatch(final LocalDateTime since, final LocalDateTime till) {
        this(true, since, till);
    }

    public TimeMatch(final boolean srcTime, final LocalDateTime since, final LocalDateTime till) {
        this(srcTime, since, till, null, null);
    }

    public TimeMatch(final boolean srcTime, final LocalDateTime since, final LocalDateTime till, final CoOp sinceOp, final CoOp tillOp) {
        this.srcTime = srcTime;
        this.since = since;
        this.till = till;
        this.sinceOp = sinceOp == null && since != null ? CoOp.greaterEqual : sinceOp;
        this.tillOp = tillOp == null && till != null ? CoOp.lower : tillOp;
    }

    @Override
    public boolean match(final Map<String, String> entry) {
        final LocalDateTime t = LocalDateTime.parse(entry.get(srcTime ? LogDoc.FieldTimeStamp : LogDoc.FieldTimeRcv), Tools.logTimeFormat);

        return (since == null || fails(t, since, sinceOp)) && (till == null || !fails(t, till, tillOp));
    }

    private boolean fails(final LocalDateTime t, final LocalDateTime mark, final CoOp op) {
        switch (op) {
            case equal:
                if (!t.equals(mark))
                    return true;
                break;
            case greater:
                if (!t.isAfter(mark))
                    return true;
                break;
            case greaterEqual:
                if (t.isBefore(mark))
                    return true;
                break;
            case lower:
                if (!t.isBefore(mark))
                    return true;
                break;
            case lowerEqual:
                if (t.isAfter(mark))
                    return true;
                break;
            case notEqual:
                if (t.equals(mark))
                    return true;
                break;
        }

        return false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TimeMatch timeMatch = (TimeMatch) o;
        return srcTime == timeMatch.srcTime && Objects.equals(since, timeMatch.since) && Objects.equals(till, timeMatch.till) && sinceOp == timeMatch.sinceOp && tillOp == timeMatch.tillOp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcTime, since, till, sinceOp, tillOp);
    }
}
