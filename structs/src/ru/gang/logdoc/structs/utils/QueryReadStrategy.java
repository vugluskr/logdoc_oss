package ru.gang.logdoc.structs.utils;

import ru.gang.logdoc.structs.enums.CoOp;
import ru.gang.logdoc.structs.enums.Join;
import ru.gang.logdoc.structs.enums.LogLevel;
import ru.gang.logdoc.structs.enums.Match;
import ru.gang.logdoc.structs.query.Condition;
import ru.gang.logdoc.structs.query.Query;
import ru.gang.logdoc.structs.query.conditions.*;
import ru.gang.logdoc.structs.utils.StreamTools.UtfReader;

import java.io.EOFException;
import java.io.InvalidObjectException;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import static ru.gang.logdoc.structs.utils.Tools.isEmpty;
import static ru.gang.logdoc.structs.utils.Tools.logTimeFormat;

public class QueryReadStrategy implements Consumer<Byte> {
    protected Consumer<Byte> strategy;
    private final Consumer<Query> successConsumer;
    private final Consumer<Throwable> errorConsumer;

    public QueryReadStrategy(final Consumer<Query> successConsumer, final Consumer<Throwable> errorConsumer) {
        this.successConsumer = successConsumer;
        this.errorConsumer = errorConsumer;

        strategy = readBody(successConsumer);
    }

    @Override
    public void accept(final Byte b) {
        if (b == -1) {
            errorConsumer.accept(new EOFException());
            return;
        }

        strategy.accept(b);
    }

    private Consumer<Byte> readBody(final Consumer<Query> callback) {
        return b -> {
            final Query q = new Query(Join.values()[b]);

            strategy = new StreamTools.ShortReader(size -> {
                if (size <= 0) {
                    callback.accept(q);
                    return;
                }

                strategy = readConditions(q, size);
            });
        };
    }

    private Consumer<Byte> readConditions(final Query q, final int limit) {
        final Consumer<Condition> packman = c -> {
            q.add(c);

            if (q.size() == limit)
                successConsumer.accept(q);
            else
                strategy = readConditions(q, limit);
        };

        return b -> {
            switch (b) {
                case 0: // Query
                    strategy = readBody(packman::accept);
                    break;
                case 1: // TimeMatch
                    strategy = useSrcTime ->
                            strategy = new UtfReader(since ->
                                    strategy = new UtfReader(till ->
                                            strategy = sinceOp ->
                                                    strategy = tillOp ->
                                                            packman.accept(new TimeMatch(
                                                                    useSrcTime != 0,
                                                                    isEmpty(since) ? null : LocalDateTime.parse(since, logTimeFormat),
                                                                    isEmpty(till) ? null : LocalDateTime.parse(till, logTimeFormat),
                                                                    CoOp.values()[sinceOp],
                                                                    CoOp.values()[tillOp]
                                                            ))));
                    break;
                case 2: // SourceMatch
                    strategy = match ->
                            strategy = new UtfReader(sample ->
                                    packman.accept(new SourceMatch(sample, Match.values()[match])));
                    break;
                case 3: // Grep
                    strategy = new UtfReader(sample ->
                            strategy = caseSensetive ->
                                    packman.accept(new Grep(sample, caseSensetive != 0)));
                    break;
                case 4: // FieldMatch
                    strategy = new UtfReader(field ->
                            strategy = match ->
                                    strategy = strict ->
                                            strategy = missField ->
                                                    strategy = new UtfReader(sample ->
                                                            packman.accept(new FieldMatch(
                                                                    field,
                                                                    Match.values()[match],
                                                                    strict != 0,
                                                                    missField != 0,
                                                                    sample
                                                            ))));
                    break;
                case 5: // LevelMatch
                    strategy = coOp ->
                            strategy = logLevel ->
                                    packman.accept(new LevelMatch(
                                            CoOp.values()[coOp],
                                            LogLevel.values()[logLevel]
                                    ));
                    break;
                case 6: // SourceIpMatch
                    strategy = match ->
                            strategy = new UtfReader(sample ->
                                    packman.accept(new SourceIpMatch(sample, Match.values()[match])));
                    break;
                case 7: // SourceIdMatch
                    strategy = match ->
                            strategy = new UtfReader(sample ->
                                    packman.accept(new SourceIdMatch(sample, Match.values()[match])));
                    break;
                default:
                    errorConsumer.accept(new InvalidObjectException("Unknown condition type: " + b + "; readed query: " + q));
            }
        };
    }
}
