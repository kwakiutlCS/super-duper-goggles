package me.ricardo.playground.ir.api.entity;

import java.time.temporal.ChronoUnit;

import javax.validation.constraints.Min;

public class TimeDto {

	@Min(0)
	private long value;
	
	private Integer step;
	
	private ChronoUnit unit;
	
	private String zone;

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

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}
}
