package me.ricardo.playground.ir.domain.entity.repetition;

import java.util.stream.Stream;

import javax.validation.constraints.PositiveOrZero;

import me.ricardo.playground.ir.domain.entity.bound.Bound;
import me.ricardo.playground.ir.utils.Utils;

public final class FixedTime implements Time {

    @PositiveOrZero
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
	
	@Override
	public Stream<Long> schedule(long offset, Bound bound) {
	    return bound.apply(schedule(offset));
	}
	
	@Override
	public boolean addException(long exception) {
	   return false; 
	}
	
	@Override
	public Time truncate(long timestamp) {
	    if (timestamp <= time) {
	        return NoTime.INSTANCE;
	    }
	    
	    return this;
	}
}
