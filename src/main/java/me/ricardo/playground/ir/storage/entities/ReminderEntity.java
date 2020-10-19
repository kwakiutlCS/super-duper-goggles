package me.ricardo.playground.ir.storage.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name = "Reminder")
public class ReminderEntity extends PanacheEntity {

	public String content;
	
	@Column(nullable = false)
	public long createdAt;

	@Column(nullable = false)
	public long updatedAt;
}
