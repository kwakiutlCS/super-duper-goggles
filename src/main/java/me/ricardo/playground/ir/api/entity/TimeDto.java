package me.ricardo.playground.ir.api.entity;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Min;

public class TimeDto {

	@Min(0)
	private long value;
	
	private Integer step;
	
	private ChronoUnit unit;
	
	private String zone;
	
	private BoundDto bound;
	
	private Set<Long> exceptions;

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

	public BoundDto getBound() {
		return bound;
	}
	
	public void setBound(BoundDto bound) {
		this.bound = bound;
	}

	public Set<Long> getExceptions() {
		return exceptions;
	}
	
	public void setExceptions(Set<Long> exceptions) {
		this.exceptions = exceptions;
	}
}
