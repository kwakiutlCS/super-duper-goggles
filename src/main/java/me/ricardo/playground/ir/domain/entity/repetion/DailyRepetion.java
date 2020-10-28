package me.ricardo.playground.ir.domain.entity.repetion;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import me.ricardo.playground.ir.utils.Utils;

public final class DailyRepetion implements Time {

	final private long start;
	
	final private Bound bound;
	
	final private int hour;
	
	final private int minute;
	
	final private int step;
	
	final private ZoneId zone;
	
	final private List<Long> exceptions;
	
	public DailyRepetion(long start) {
		this(start, 1, Bound.none(), ZoneOffset.UTC);
	}
	
	public DailyRepetion(long start, int step, Bound bound, ZoneId zone) {
		this(start, step, bound, zone, List.of());
	}
	
	public DailyRepetion(long start, int step, Bound bound, ZoneId zone, List<Long> exceptions) {
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
	
	public List<Long> getExceptions() {
		return exceptions;
	}
	
	@Override
	public Stream<Long> schedule() {
		return schedule(start);
	}

	@Override
	public Stream<Long> schedule(long offset) {
		var lowerBound = ZonedDateTime.ofInstant(Instant.ofEpochSecond(start), zone);
		long iterations = 0;
		
		if (offset > start) {
			iterations = ChronoUnit.DAYS.between(Instant.ofEpochSecond(start), Instant.ofEpochSecond(offset)) / step;
			
			if (offset > start + iterations * step * ChronoUnit.DAYS.getDuration().getSeconds()) {
				iterations++;
			}
			
		    lowerBound = lowerBound.truncatedTo(ChronoUnit.DAYS)
		    					   .plusDays(iterations * step)
		    					   .withHour(hour)
		    					   .withMinute(minute);
		}
		
		var noBoundSchedule = Stream.iterate(lowerBound, v -> v.plusDays(step))
			     	 	     		.map(v -> v.withHour(hour))
			     	 	     		.map(ZonedDateTime::toEpochSecond);
		
		return bound(noBoundSchedule, iterations)
			       .filter(v -> !exceptions.contains(v));
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
