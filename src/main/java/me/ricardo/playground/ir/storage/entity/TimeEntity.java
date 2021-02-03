package me.ricardo.playground.ir.storage.entity;

import java.time.temporal.ChronoUnit;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name = "Time")
public class TimeEntity extends PanacheEntity {

	public long time;
	
	public Long minute;
	
	public Integer step;
	
	public ChronoUnit unit;
	
	public String zone;
	
	public BoundType boundType;
	
	public Long boundValue;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name="Exception")
	public Set<Long> exceptions;
}
