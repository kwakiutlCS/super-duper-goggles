package me.ricardo.playground.ir.api.entity;

import javax.validation.Valid;

public class ReminderDto {

	private Long id;
	
	private String content;

	private Long createdAt;
	
	private Long updatedAt;
	
	@Valid
	private TimeDto time;
	
	public Long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
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
	
	public TimeDto getTime() {
		return time;
	}
	
	public void setTime(TimeDto time) {
		this.time = time;
	}
}
