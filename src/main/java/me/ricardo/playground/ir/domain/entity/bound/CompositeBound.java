package me.ricardo.playground.ir.domain.entity.bound;

import java.util.stream.Stream;

import javax.validation.Valid;

record CompositeBound(@Valid Bound left, @Valid Bound right) implements Bound {

    @Override
    public Stream<Long> apply(Stream<Long> schedule) {
        return apply(schedule, 0L);
    }
    
    @Override
    public Stream<Long> apply(Stream<Long> schedule, long iterations) {
        return left.apply(right.apply(schedule, iterations), iterations);
    }

    @Override
    public boolean isAfter(long timestamp) {
        return left.isAfter(timestamp) && right.isAfter(timestamp);
    }
    
    @Override
    public boolean isBounded() {
        return true;
    }

    @Override
    public Bound add(Bound other) {
        return other.isBounded() ? new CompositeBound(this, other) : this;
    }
}
