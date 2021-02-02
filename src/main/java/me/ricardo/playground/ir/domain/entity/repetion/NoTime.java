package me.ricardo.playground.ir.domain.entity.repetion;

import java.util.stream.Stream;

import me.ricardo.playground.ir.domain.entity.bound.Bound;

public final class NoTime implements Time {

    public static final NoTime INSTANCE = new NoTime();
    
    private NoTime() { }
    
    @Override
    public Stream<Long> schedule() {
        return Stream.empty();
    }

    @Override
    public Stream<Long> schedule(long start) {
        return Stream.empty();
    }
    
    @Override
    public Stream<Long> schedule(long start, Bound bound) {
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
