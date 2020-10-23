package me.ricardo.playground.ir.api.entity;

import java.time.temporal.ChronoUnit;

public class TimeDto {

	private long value;
	
	private Integer step;
	
	private ChronoUnit unit;

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public ChronoUnit getUnit() {
		return unit;
	}

	public void setUnit(ChronoUnit unit) {
		this.unit = unit;
	}
}
