package me.ricardo.playground.ir.domain.entity.repetion;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import me.ricardo.playground.ir.domain.entity.Time;
import me.ricardo.playground.ir.utils.Utils;

public class DailyRepetion implements Time {

	private static final long DAY = 86400;
	
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

	@Override
	public Stream<Long> schedule() {
		return schedule(start);
	}

	@Override
	public Stream<Long> schedule(long offset) {
		ZonedDateTime lowerBound = ZonedDateTime.ofInstant(Instant.ofEpochSecond(start), zone);
		
		if (offset > start) {
			long steps = ChronoUnit.DAYS.between(Instant.ofEpochSecond(start), Instant.ofEpochSecond(offset)) / step;
			
			if (offset > start + steps * step * DAY) {
				steps++;
			}
			
		    lowerBound = lowerBound.truncatedTo(ChronoUnit.DAYS)
		    					   .plusDays(steps * step)
		    					   .withHour(hour)
		    					   .withMinute(minute);
		}
		
		return boundSchedule(Stream.iterate(lowerBound, v -> v.plusDays(step))
			     	               .map(v -> v.withHour(hour)))
			       .map(ZonedDateTime::toEpochSecond);
	}
	
	private Stream<ZonedDateTime> boundSchedule(Stream<ZonedDateTime> schedule) {
		switch (bound.getType()) {
		
		case COUNT_BOUND:
			return schedule.limit(bound.getLimit());
			
		case TIMESTAMP_BOUND:
			ZonedDateTime timeBound = ZonedDateTime.ofInstant(Instant.ofEpochSecond(bound.getTimestamp()), zone);
			return schedule.takeWhile(v -> !v.isAfter(timeBound));
			
		case NO_BOUND:
		default:
			return schedule;
		}
	}
}
