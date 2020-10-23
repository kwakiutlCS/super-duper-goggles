package me.ricardo.playground.ir.domain.entity.repetion;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import me.ricardo.playground.ir.utils.Utils;

public final class DailyRepetion implements Time {

	private long start;
	
	private Bound bound;
	
	private int hour;
	
	private int minute;
	
	private int step;
	
	private ZoneId zone;
	
	public DailyRepetion(long start) {
		this(start, 1, Bound.none(), ZoneOffset.UTC);
	}
	
	public DailyRepetion(long start, int step, Bound bound, ZoneId zone) {
		this.start = Utils.truncateToMinute(start);
		this.bound = bound;
		this.step = step;
		this.zone = zone;
		this.hour = Utils.parseHour(this.start, zone);
		this.minute = Utils.parseMinute(this.start, zone);
	}

	public long getStart() {
		return start;
	}
	
	public int getStep() {
		return step;
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
		
		return bound(noBoundSchedule, iterations);
	}
	
	private Stream<Long> bound(Stream<Long> schedule, long iterations) {
		switch (bound.getType()) {
		case COUNT_BOUND:
			if (iterations >= bound.getLimit()) {
				return Stream.empty();
			}
			return schedule.limit(bound.getLimit() - iterations);
			
		case TIMESTAMP_BOUND:
			return schedule.takeWhile(v -> v <= bound.getTimestamp());
			
		case NO_BOUND:
		default:
			return schedule;
		}
	}
}
