package me.ricardo.playground.ir.domain.entity;

import java.util.stream.Stream;

import javax.json.bind.annotation.JsonbCreator;

public class Reminder {

	private Long id;
	
	private String content;

	private Long createdAt;
	
	private Long updatedAt;
	
	private Time time;
	
	@JsonbCreator
	public Reminder(String content) {
		this.content = content;
	}
	
	public Long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(long updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public Time getTime() {
		return time;
	}
	
	public void setTime(Time time) {
		this.time = time;
	}

	public Stream<Long> schedule() {
		return time == null ? Stream.empty() : time.schedule();
	}
	
	public Stream<Long> schedule(long start) {
		return time == null ? Stream.empty() : time.schedule(start);
	}
}
