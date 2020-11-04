package me.ricardo.playground.ir.domain.entity.repetion;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import me.ricardo.playground.ir.utils.Utils;

public final class DailyRepetion implements Time {

	private final long start;
	
	private final Bound bound;
	
	private final int hour;
	
	private final int minute;
	
	private final int step;
	
	private final ZoneId zone;
	
	private final Set<Long> exceptions;
	
	public DailyRepetion(long start) {
		this(start, 1, Bound.none(), ZoneOffset.UTC);
	}
	
	public DailyRepetion(long start, int step, Bound bound, ZoneId zone) {
		this(start, step, bound, zone, Set.of());
	}
	
	public DailyRepetion(long start, int step, Bound bound, ZoneId zone, Set<Long> exceptions) {
		this.start = Utils.truncateToMinute(start);
		this.bound = bound;
		this.step = step;
		this.zone = zone;
		this.hour = Utils.parseHour(this.start, zone);
		this.minute = Utils.parseMinute(this.start, zone);
		this.exceptions = exceptions;
	}

	public long getStart() {
		return start;
	}
	
	public int getStep() {
		return step;
	}

	public ZoneId getZone() {
		return zone;
	}

	public Bound getBound() {
		return bound;
	}
	
	public Set<Long> getExceptions() {
		return exceptions;
	}
	
	@Override
	public Stream<Long> schedule() {
		return schedule(start);
	}

	@Override
	public Stream<Long> schedule(long offset) {
		var startDate = ZonedDateTime.ofInstant(Instant.ofEpochSecond(start), zone);
		
		var iterations = calculateNumberIterations(startDate, offset);
		
		var lowerBound = startDate.plusDays(iterations * step);
		
		var noBoundSchedule = Stream.iterate(lowerBound, v -> v.plusDays(step))
			     	 	     		.map(ZonedDateTime::toEpochSecond);
		
		return bound(noBoundSchedule, iterations)
			       .filter(v -> !exceptions.contains(v));
	}
	
	
	private long calculateNumberIterations(ZonedDateTime startDate, long offset) {
		if (offset <= start) return 0;
		
		var fullIterations = ChronoUnit.DAYS.between(Instant.ofEpochSecond(start), Instant.ofEpochSecond(offset)) / step;
		var partialIterations = offset > startDate.plusDays(fullIterations * step).toEpochSecond() ? 1 : 0;
		
		return fullIterations + partialIterations;
	}
	
	
	private Stream<Long> bound(Stream<Long> schedule, long iterations) {
		switch (bound.getType()) {
		case COUNT_BOUND:
			return schedule.limit(Math.max(0, bound.getLimit() - iterations));
			
		case TIMESTAMP_BOUND:
			return schedule.takeWhile(v -> v <= bound.getTimestamp());
			
		case NO_BOUND:
		default:
			return schedule;
		}
	}
}
