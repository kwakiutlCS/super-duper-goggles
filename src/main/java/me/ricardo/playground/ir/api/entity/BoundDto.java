package me.ricardo.playground.ir.api.entity;

import javax.validation.constraints.Min;

public class BoundDto {

	@Min(0)
	private Long timestamp;
	
	@Min(1)
	private Long limit;

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long time) {
		this.timestamp = time;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		return "Bound [timestamp=" + timestamp + ", limit=" + limit + "]";
	}
}
