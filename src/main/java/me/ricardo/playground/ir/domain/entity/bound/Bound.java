package me.ricardo.playground.ir.domain.entity.bound;

import java.util.stream.Stream;

import me.ricardo.playground.ir.domain.validation.BoundConstraint;

public sealed interface Bound {
    Bound add(Bound bound);
    
    Stream<Long> apply(Stream<Long> schedule);
    
    Stream<Long> apply(Stream<Long> schedule, long iterations);
    
    public static SingleBound none() {
        return new SingleBound(BoundType.NO_BOUND, 0, 0);
    }
    
    public static SingleBound timestamp(long timestamp) {
        return new SingleBound(BoundType.TIMESTAMP_BOUND, 0, timestamp);
    }
    
    public static SingleBound count(long limit) {
        return new SingleBound(BoundType.COUNT_BOUND, limit, 0);
    }
    
    public static Bound combined(Bound left, Bound right) {
        return left.add(right);
    }

    public enum BoundType {
        NO_BOUND, COUNT_BOUND, TIMESTAMP_BOUND;
    }

    @BoundConstraint
    public record SingleBound(BoundType type, long limit, long timestamp) implements Bound {
    	@Override
    	public Bound add(Bound bound) {
    	    if (this.type == BoundType.NO_BOUND) return bound;
    	    if (bound instanceof SingleBound b && b.type == BoundType.NO_BOUND) return this;
    	    return new CombinedBound(this, bound);
    	}
    	
    	@Override
    	public Stream<Long> apply(Stream<Long> schedule) {
    	    return apply(schedule, 0);
    	}
    	
    	@Override
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
    
    record CombinedBound(Bound left, Bound right) implements Bound {
        @Override
        public Bound add(Bound bound) {
            if (bound instanceof SingleBound b && b.type == BoundType.NO_BOUND) return this;
            return new CombinedBound(this, bound);
        }

        @Override
        public Stream<Long> apply(Stream<Long> schedule) {
            return apply(schedule, 0);
        }

        @Override
        public Stream<Long> apply(Stream<Long> schedule, long iterations) {
            return right.apply(left.apply(schedule, iterations));
        }
        
    }
}
