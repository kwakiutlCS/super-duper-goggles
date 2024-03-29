package me.ricardo.playground.ir.domain.entity.bound;

import java.util.stream.Stream;

import javax.validation.constraints.Positive;

public record TimeBound(@Positive long timestamp) implements GuaranteedBound {

    @Override
    public Stream<Long> apply(Stream<Long> schedule) {
        return apply(schedule, 0);
    }

    @Override
    public Stream<Long> apply(Stream<Long> schedule, long iterations) {
        return schedule.takeWhile(s -> s <= timestamp);
    }

    @Override
    public boolean isAfter(long timestamp) {
        return this.timestamp > timestamp;
    }

    @Override
    public long getValue() {
        return timestamp;
    }
}
