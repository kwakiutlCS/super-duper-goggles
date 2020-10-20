package me.ricardo.playground.ir.domain.entity;

import java.util.stream.Stream;

public class DailyRepetion implements Time {

	private static final long  DAY = 86400;
	
	private long start;
	
	private Long end;
	
	private int step;
	
	public DailyRepetion(long start) {
		this.start = start;
		this.step = 1;
	}
	
	public DailyRepetion(long start, int step, Long end) {
		this.start = start;
		this.end = end;
		this.step = step;
	}

	@Override
	public Stream<Long> schedule() {
		return schedule(start);
	}

	@Override
	public Stream<Long> schedule(long offset) {
		long first = offset <= start ? start : (((offset - start) / DAY + ((offset - start) % DAY > 0 ? 1 : 0))  * DAY) + start;
		
		return Stream.iterate(first, v -> v + step * DAY)
			     	 .takeWhile(v -> end == null || v <= end);
	}
}
