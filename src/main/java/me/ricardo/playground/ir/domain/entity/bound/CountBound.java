package me.ricardo.playground.ir.domain.entity.bound;

import java.util.stream.Stream;

import javax.validation.constraints.Positive;

public record CountBound(@Positive long limit) implements GuaranteedBound {

    @Override
    public Stream<Long> apply(Stream<Long> schedule) {
        return apply(schedule, 0L);
    }

    @Override
    public Stream<Long> apply(Stream<Long> schedule, long iterations) {
        return schedule.limit(Math.max(0, limit - iterations));
    }

    @Override
    public boolean isAfter(long timestamp) {
        return true;
    }

    @Override
    public long getValue() {
        return limit;
    }
}
