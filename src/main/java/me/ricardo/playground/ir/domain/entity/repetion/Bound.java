package me.ricardo.playground.ir.domain.entity.repetion;

import java.util.stream.Stream;

public record Bound(BoundType type, long limit, long timestamp) {

	public enum BoundType {
		NO_BOUND, COUNT_BOUND, TIMESTAMP_BOUND;
	}
	
	public static Bound none() {
		return new Bound(BoundType.NO_BOUND, 0, 0);
	}
	
	public static Bound timestamp(long timestamp) {
		return new Bound(BoundType.TIMESTAMP_BOUND, 0, timestamp);
	}
	
	public static Bound count(long limit) {
		return new Bound(BoundType.COUNT_BOUND, limit, 0);
	}
	
	public Stream<Long> apply(Stream<Long> schedule) {
	    return apply(schedule, 0);
	}
	
	public Stream<Long> apply(Stream<Long> schedule, long iterations) {
	    switch (type) {
        case COUNT_BOUND:
            return schedule.limit(Math.max(0, limit-iterations));
        
        case TIMESTAMP_BOUND:
            return schedule.takeWhile(s -> s <= timestamp);
            
        case NO_BOUND:
        default:
            return schedule;
	    }
	}
}
