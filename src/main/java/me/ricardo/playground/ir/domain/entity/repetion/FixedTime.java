package me.ricardo.playground.ir.domain.entity.repetion;

import java.util.stream.Stream;

import me.ricardo.playground.ir.utils.Utils;

public final class FixedTime implements Time {

	private final long time;
	
	public FixedTime(long time) {
		this.time = Utils.truncateToMinute(time);
	}

	public long getTime() {
		return time;
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
