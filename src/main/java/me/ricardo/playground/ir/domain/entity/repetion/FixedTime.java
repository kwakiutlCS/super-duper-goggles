package me.ricardo.playground.ir.domain.entity.repetion;

import java.util.stream.Stream;

import me.ricardo.playground.ir.domain.entity.Time;
import me.ricardo.playground.ir.utils.Utils;

public class FixedTime implements Time {

	private long time;
	
	public FixedTime(long time) {
		this.time = Utils.truncateToMinute(time);
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
