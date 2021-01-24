package me.ricardo.playground.ir.domain.entity.repetion;

import java.util.stream.Stream;

public sealed interface Time permits FixedTime, DailyRepetion {

	Stream<Long> schedule();

	Stream<Long> schedule(long start);

    boolean addException(long exception);
}
