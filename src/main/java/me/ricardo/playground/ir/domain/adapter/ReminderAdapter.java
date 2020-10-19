package me.ricardo.playground.ir.domain.adapter;

import me.ricardo.playground.ir.domain.entities.Reminder;
import me.ricardo.playground.ir.storage.entities.ReminderEntity;

public class ReminderAdapter {
	
	public static ReminderEntity toStorage(Reminder reminder) {
		ReminderEntity entity = new ReminderEntity();
		entity.content = reminder.getContent();
		entity.createdAt = reminder.getCreatedAt();
		entity.updatedAt = reminder.getUpdatedAt();
		
		return entity;
	}
	
	public static Reminder fromStorage(ReminderEntity entity) {
		Reminder reminder = new Reminder(entity.content);
		reminder.setId(entity.id);
		reminder.setCreatedAt(entity.createdAt);
		reminder.setUpdatedAt(entity.updatedAt);
		
		return reminder;
	}
}
