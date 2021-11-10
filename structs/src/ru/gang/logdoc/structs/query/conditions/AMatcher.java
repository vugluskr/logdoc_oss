package ru.gang.logdoc.structs.query.conditions;


import ru.gang.logdoc.structs.enums.Match;

import java.util.Objects;
import java.util.regex.Pattern;

public abstract class AMatcher {
    public final String sample;
    public final Match match;

    private Pattern pattern;

    protected AMatcher(final String sample, final Match match) {
        if (sample == null || sample.trim().isEmpty() || match == null)
            throw new IllegalArgumentException();

        this.sample = sample;
        this.match = match;

        if (match == Match.Patterned)
            pattern = Pattern.compile(sample);
    }

    protected boolean doesMatch(final String v) {
        switch (match) {
            case Equals: return v.equals(sample);
            case Contains: return v.contains(sample);
            case ContainsCI: return v.toLowerCase().contains(sample.toLowerCase());
            case NotContainsCI: return !v.toLowerCase().contains(sample.toLowerCase());
            case EndsWith: return v.endsWith(sample);
            case EndsWithCI: return v.toLowerCase().endsWith(sample);
            case NotContains: return !v.contains(sample);
            case NotEndsWith: return !v.endsWith(sample);
            case NotEndsWithCI: return !v.toLowerCase().endsWith(sample);
            case NotEquals: return !v.equals(sample);
            case NotStartsWith: return !v.startsWith(sample);
            case NotStartsWithCI: return !v.toLowerCase().startsWith(sample);
            case Patterned: return pattern.matcher(v).matches();
            case StartsWith: return v.startsWith(sample);
            case StartsWithCI: return v.toLowerCase().startsWith(sample);
        };

        return false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || !getClass().isAssignableFrom(o.getClass())) return false;
        final AMatcher aMatcher = (AMatcher) o;
        return sample.equals(aMatcher.sample) && match == aMatcher.match && Objects.equals(pattern, aMatcher.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sample, match, pattern);
    }
}
