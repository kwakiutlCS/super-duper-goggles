package me.ricardo.playground.ir.api.entity;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import me.ricardo.playground.ir.api.validator.Bound;
import me.ricardo.playground.ir.api.validator.Unit;
import me.ricardo.playground.ir.api.validator.Zone;

public class TimeDto {

	@NotNull @Min(0)
	private Long value;
	
	@Min(1)
	private Integer step;
	
	@Unit
	private String unit;
	
	@Zone
	private String zone;
	
	@Valid @Bound
	private BoundDto bound;
	
	private Set<@Min(0) Long> exceptions;

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

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
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

	@Override
	public String toString() {
		return "Time [value=" + value + ", step=" + step + ", unit=" + unit + ", zone=" + zone + ", bound=" + bound
				+ ", exceptions=" + exceptions + "]";
	}
}
