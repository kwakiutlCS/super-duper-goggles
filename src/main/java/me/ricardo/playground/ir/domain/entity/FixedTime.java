package me.ricardo.playground.ir.domain.entity;

import java.util.stream.Stream;

public class FixedTime implements Time {

	private long time;
	
	public FixedTime(long time) {
		this.time = time;
	}
	
	@Override
	public Stream<Long> schedule() {
		return Stream.of(time);
	}

	@Override
	public Stream<Long> schedule(long offset) {
		return offset > time ? Stream.empty() : schedule();
	}
}
