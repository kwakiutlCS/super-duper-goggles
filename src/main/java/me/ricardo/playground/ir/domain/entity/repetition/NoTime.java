package me.ricardo.playground.ir.domain.entity.repetition;

import java.util.stream.Stream;

import me.ricardo.playground.ir.domain.entity.bound.GuaranteedBound;

public final class NoTime implements Time {

    public static final NoTime INSTANCE = new NoTime();
    
    private NoTime() { }
    
    @Override
    public Stream<Long> schedule(long start, GuaranteedBound bound) {
        return Stream.empty(); 
    }

    @Override
    public boolean addException(long exception) {
        return false;
    }

    @Override
    public Time truncate(long timestamp) {
        return INSTANCE;
    }
}
