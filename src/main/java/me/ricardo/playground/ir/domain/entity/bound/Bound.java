package me.ricardo.playground.ir.domain.entity.bound;

import java.util.Objects;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

public sealed interface Bound permits AtomicBound, CompositeBound {

    public static NoBound none() {
        return NoBound.INSTANCE;
    }
    
    public static TimeBound timestamp(long timestamp) {
        return new TimeBound(timestamp);
    }
    
    public static CountBound count(long limit) {
        return new CountBound(limit);
    }
    
    static Bound composite(Bound bound1, Bound bound2) {
        Objects.requireNonNull(bound1);
        Objects.requireNonNull(bound2);
        return bound1.add(bound2);
    }

    Stream<Long> apply(Stream<Long> schedule);

    Stream<Long> apply(Stream<Long> schedule, long iterations);
    
    boolean isBounded();

    Bound add(@NotNull Bound other);

    boolean isAfter(long timestamp);
}
