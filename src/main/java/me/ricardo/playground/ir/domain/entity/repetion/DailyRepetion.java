package me.ricardo.playground.ir.domain.entity.repetion;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import me.ricardo.playground.ir.domain.validation.BoundConstraint;
import me.ricardo.playground.ir.domain.validation.StartEndConsistent;
import me.ricardo.playground.ir.utils.Utils;

@StartEndConsistent
public final class DailyRepetion implements Time {

    @PositiveOrZero
	private final long start;
	
	@BoundConstraint
	private final Bound bound;
	
	@Positive
	private final int step;
	
	private final ZoneId zone;
	
	private final Set<Long> exceptions;
	
	public DailyRepetion(long start) {
		this(start, 1, Bound.none(), ZoneOffset.UTC);
	}
	
	public DailyRepetion(long start, int step, Bound bound, ZoneId zone) {
		this(start, step, bound, zone, new HashSet<>());
	}
	
	public DailyRepetion(long start, int step, Bound bound, ZoneId zone, Set<Long> exceptions) {
		this.start = Utils.truncateToMinute(start);
		this.bound = bound;
		this.step = step;
		this.zone = zone;
		this.exceptions = exceptions == null ? new HashSet<>() : filterValidExceptions(exceptions);
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

    public boolean addException(long exception) {
        if (!isExceptionValid(exception)) {
            return false;
        }
        
        return this.exceptions.add(exception); 
    }
    
	@Override
	public Stream<Long> schedule() {
		return schedule(start);
	}

	@Override
	public Stream<Long> schedule(long offset) {
		return scheduleBeforeExceptions(offset).filter(s -> !exceptions.contains(s));
	}
	
	
	private Stream<Long> scheduleBeforeExceptions(long offset) {
		var startDate = ZonedDateTime.ofInstant(Instant.ofEpochSecond(start), zone);
		
		var iterations = calculateNumberIterations(startDate, offset);
		
		var lowerBound = startDate.plusDays(iterations * step);
		
		var noBoundSchedule = Stream.iterate(lowerBound, v -> v.plusDays(step))
			     	 	     		.map(ZonedDateTime::toEpochSecond);
		
		return bound(noBoundSchedule, iterations);
	}
	
	
	private long calculateNumberIterations(ZonedDateTime startDate, long offset) {
		if (offset <= start) return 0;
		
		var fullIterations = ChronoUnit.DAYS.between(Instant.ofEpochSecond(start), Instant.ofEpochSecond(offset)) / step;
		var partialIterations = offset > startDate.plusDays(fullIterations * step).toEpochSecond() ? 1 : 0;
		
		return fullIterations + partialIterations;
	}
	
	
	private Stream<Long> bound(Stream<Long> schedule, long iterations) {
		switch (bound.type()) {
		case COUNT_BOUND:
			return schedule.limit(Math.max(0, bound.limit() - iterations));
			
		case TIMESTAMP_BOUND:
			return schedule.takeWhile(v -> v <= bound.timestamp());
			
		case NO_BOUND:
		default:
			return schedule;
		}
	}
	
	
	private Set<Long> filterValidExceptions(Set<Long> exceptions) {
		return exceptions.stream()
		                 .filter(this::isExceptionValid)
		                 .collect(Collectors.toSet());
	}
	
	
	private boolean isExceptionValid(long exception) {
	    return scheduleBeforeExceptions(exception).takeWhile(s -> s <= exception)
	                                              .findAny()
	                                              .isPresent();
	}
}
