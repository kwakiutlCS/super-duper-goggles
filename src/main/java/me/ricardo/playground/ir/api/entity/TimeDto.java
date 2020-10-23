package me.ricardo.playground.ir.api.entity;

import javax.json.bind.annotation.JsonbCreator;

public class TimeDto {

	private long value;
	
	@JsonbCreator
	public TimeDto(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}
}
