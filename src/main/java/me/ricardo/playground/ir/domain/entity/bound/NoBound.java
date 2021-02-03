package me.ricardo.playground.ir.domain.entity.bound;

import java.util.stream.Stream;

public final class NoBound implements AtomicBound {

    public static final NoBound INSTANCE = new NoBound();
    
    public static final String BOUND_TYPE = "NO_BOUND";
    
    private NoBound() {}

    @Override
    public Stream<Long> apply(Stream<Long> schedule) {
        return schedule;
    }
    
    @Override
    public Stream<Long> apply(Stream<Long> schedule, long iterations) {
        return schedule;
    }

    @Override
    public boolean isBounded() {
        return false;
    }

    @Override
    public Bound add(Bound other) {
        return other;
    }

    @Override
    public boolean isAfter(long timestamp) {
        return true;
    }

    @Override
    public long getValue() {
        return 0;
    }

    @Override
    public String getType() {
        return BOUND_TYPE;
    }
}
