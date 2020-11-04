package me.ricardo.playground.ir.domain.entity.repetion;

public class Bound {

	public enum BoundType {
		NO_BOUND, COUNT_BOUND, TIMESTAMP_BOUND;
	}
	
	private final BoundType type;
	
	private final long limit;
	
	private final long timestamp;
	
	private Bound(BoundType type, long limit, long timestamp) {
		this.type = type;
		this.limit = limit;
		this.timestamp = timestamp;
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
