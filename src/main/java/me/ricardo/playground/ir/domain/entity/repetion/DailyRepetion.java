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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import me.ricardo.playground.ir.domain.entity.bound.Bound;
import me.ricardo.playground.ir.domain.entity.bound.Bound.SingleBound;
import me.ricardo.playground.ir.domain.validation.StartEndConsistent;
import me.ricardo.playground.ir.utils.Utils;

@StartEndConsistent
public final class DailyRepetion implements Time {

    @PositiveOrZero
	private final long start;
	
    @Valid
	private final SingleBound bound;
	
	@Positive
	private final int step;
	
	@NotNull
	private final ZoneId zone;
	
	private final Set<Long> exceptions;
	
	public DailyRepetion(long start) {
		this(start, 1, Bound.none(), ZoneOffset.UTC);
	}
	
	public DailyRepetion(long start, int step, SingleBound bound, ZoneId zone) {
		this(start, step, bound, zone, new HashSet<>());
	}
	
	public DailyRepetion(long start, int step, SingleBound bound, ZoneId zone, Set<Long> exceptions) {
		this.start = Utils.truncateToMinute(start);
		this.bound = bound != null ? bound : Bound.none();
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

	public SingleBound getBound() {
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
		return schedule(offset, Bound.none());
	}

    @Override
    public Stream<Long> schedule(long offset, Bound externalBound) {
        return scheduleBeforeExceptions(offset, externalBound).filter(s -> !exceptions.contains(s));
    }
    
    @Override
    public Time truncate(long timestamp) {
        if (timestamp <= start)
            return NoTime.INSTANCE;
        
        return new DailyRepetion(start, step, Bound.timestamp(timestamp-1), zone, exceptions);
    }	
    
	private Stream<Long> scheduleBeforeExceptions(long offset, Bound externalBound) {
		var startDate = ZonedDateTime.ofInstant(Instant.ofEpochSecond(start), zone);
		
		var iterations = calculateNumberIterations(startDate, offset);
		
		var lowerBound = startDate.plusDays(iterations * step);
		
		var noBoundSchedule = Stream.iterate(lowerBound, v -> v.plusDays(step))
			     	 	     		.map(ZonedDateTime::toEpochSecond);
		
		return bound.add(externalBound).apply(noBoundSchedule, iterations);
	}
	
	
	private long calculateNumberIterations(ZonedDateTime startDate, long offset) {
		if (offset <= start) return 0;
		
		var fullIterations = ChronoUnit.DAYS.between(Instant.ofEpochSecond(start), Instant.ofEpochSecond(offset)) / step;
		var partialIterations = offset > startDate.plusDays(fullIterations * step).toEpochSecond() ? 1 : 0;
		
		return fullIterations + partialIterations;
	}
	
	
	private Set<Long> filterValidExceptions(Set<Long> exceptions) {
		return exceptions.stream()
		                 .filter(this::isExceptionValid)
		                 .collect(Collectors.toSet());
	}
	
	
	private boolean isExceptionValid(long exception) {
	    return scheduleBeforeExceptions(exception, Bound.timestamp(exception))
	            .findAny()
	            .isPresent();
	}

}
