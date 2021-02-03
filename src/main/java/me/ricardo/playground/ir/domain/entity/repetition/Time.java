package me.ricardo.playground.ir.domain.entity.repetition;

import java.util.stream.Stream;

import me.ricardo.playground.ir.domain.entity.bound.Bound;

public sealed interface Time permits FixedTime, DailyRepetition, NoTime {

	Stream<Long> schedule();

	Stream<Long> schedule(long start);
	
	Stream<Long> schedule(long start, Bound bound);

    boolean addException(long exception);
    
    Time truncate(long timestamp);
}
