package me.ricardo.playground.ir.domain.entity.repetion;

public class Bound {

	public enum BoundType {
		NO_BOUND, COUNT_BOUND, TIMESTAMP_BOUND;
	}
	
	private BoundType type;
	
	private long limit;
	
	private long timestamp;
	
	private Bound(BoundType type) {
		this.type = type;
	}
	
	public static Bound none() {
		return new Bound(BoundType.NO_BOUND);
	}
	
	public static Bound timestamp(long timestamp) {
		Bound bound = new Bound(BoundType.TIMESTAMP_BOUND);
		bound.timestamp = timestamp;
		
		return bound;
	}
	
	public static Bound count(long limit) {
		Bound bound = new Bound(BoundType.COUNT_BOUND);
		bound.limit = limit;
		
		return bound;
	}
	
	public long getLimit() {
		return limit;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public BoundType getType() {
		return type;
	}
}
