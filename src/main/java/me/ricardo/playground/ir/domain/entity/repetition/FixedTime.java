package me.ricardo.playground.ir.domain.entity.repetition;

import java.util.stream.Stream;

import javax.validation.constraints.PositiveOrZero;

import me.ricardo.playground.ir.domain.entity.bound.GuaranteedBound;
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
    public Stream<Long> schedule(long offset, GuaranteedBound bound) {
        return bound.apply(offset > time ? Stream.empty() : Stream.of(time));
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
