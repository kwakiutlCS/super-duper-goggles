package me.ricardo.playground.ir.domain.entity;

import java.util.stream.Stream;

public interface Time {

	Stream<Long> schedule();

	Stream<Long> schedule(long start);
}
