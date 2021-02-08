package me.ricardo.playground.ir.domain.entity.repetition;

import java.util.stream.Stream;

import me.ricardo.playground.ir.domain.entity.bound.GuaranteedBound;

public sealed interface Time permits FixedTime, DailyRepetition, NoTime {

    Stream<Long> schedule(long start, GuaranteedBound bound);

    boolean addException(long exception);
    
    Time truncate(long timestamp);
}
