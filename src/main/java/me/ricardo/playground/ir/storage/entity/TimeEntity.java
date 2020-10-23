package me.ricardo.playground.ir.storage.entity;

import java.time.temporal.ChronoUnit;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name = "Time")
public class TimeEntity extends PanacheEntity {

	public long time;
	
	public Integer step;
	
	public ChronoUnit unit;
	
	public String zone;
	
	public Integer boundType;
	
	public Long boundValue;
}
