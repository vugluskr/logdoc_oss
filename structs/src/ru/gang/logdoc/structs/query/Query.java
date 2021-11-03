package ru.gang.logdoc.structs.query;

import ru.gang.logdoc.structs.enums.Join;
import ru.gang.logdoc.structs.utils.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class Query extends ArrayList<Condition> implements Condition, Comparable<Query> {
    public final Join join;

    public Query() {
        this.join = Join.AND;
    }

    public Query(final Join join) {
        super(2);
        this.join = join;
    }

    public Query(final Condition and) {
        this(Join.AND);
        add(and);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;


        final Query that = (Query) o;
        if (join != that.join || this.size() != that.size())
            return false;

        for (final Condition c : this)
            if (!that.contains(c))
                return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), join);
    }

    @Override
    public int compareTo(final Query o) {

        return 0;
    }

    @Override
    public boolean match(final Map<String, String> dto) {
        if (Tools.isEmpty(this))
            return true;

        if (join == Join.AND) {
            for (final Condition c : this)
                if (!c.match(dto))
                    return false;

            return true;
        }

        for (final Condition c : this)
            if (c.match(dto))
                return true;

        return false;
    }

    public static Query matchAll() {
        return new Query(Join.AND);
    }

    public static Query or(final Condition... conditions) {
        return q(Join.OR, conditions);
    }

    public static Query and(final Condition... conditions) {
        return q(Join.AND, conditions);
    }

    public static Query q(final Join join, final Condition... conditions) {
        if (Tools.isEmpty(conditions))
            throw new IllegalArgumentException();

        final Query q = new Query(join);
        Arrays.stream(conditions)
                .filter(Objects::nonNull)
                .forEach(q::add);

        return q;
    }
}
