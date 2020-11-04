package me.ricardo.playground.ir.domain.entity;

public class Metadata {

	private final long createdAt;
	
	private final long updatedAt;
	
	public Metadata(long createdAt) {
		this.createdAt = createdAt;
		this.updatedAt = createdAt;
	}
	
	public Metadata(long createdAt, long updatedAt) {
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public long getCreatedAt() {
		return createdAt;
	}
	
	public long getUpdatedAt() {
		return updatedAt;
	}
}
