package me.ricardo.playground.ir.domain.entities;

import javax.json.bind.annotation.JsonbCreator;

public class Reminder {

	private Long id;
	
	private String content;

	private Long createdAt;
	
	private Long updatedAt;
	
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
}
