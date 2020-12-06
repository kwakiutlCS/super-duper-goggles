package me.ricardo.playground.ir.storage.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name = "Reminder")
public class ReminderEntity extends PanacheEntity {

	public String content;
	
	@Column(nullable = false)
	public String userId;
	
	public long createdAt;

	public long updatedAt;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	public TimeEntity time;
}
