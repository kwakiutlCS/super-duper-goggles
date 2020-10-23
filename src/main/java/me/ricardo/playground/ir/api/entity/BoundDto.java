package me.ricardo.playground.ir.api.entity;

public class BoundDto {

	private Long timestamp;
	
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
}
