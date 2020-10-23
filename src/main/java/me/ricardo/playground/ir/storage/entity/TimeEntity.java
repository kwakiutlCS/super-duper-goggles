package me.ricardo.playground.ir.storage.entity;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name = "Time")
public class TimeEntity extends PanacheEntity {

	public Long time;
}
