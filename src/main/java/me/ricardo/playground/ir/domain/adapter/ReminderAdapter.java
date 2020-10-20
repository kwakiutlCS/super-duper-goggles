package me.ricardo.playground.ir.domain.adapter;

import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;

public class ReminderAdapter {
	
	public static ReminderEntity toStorage(Reminder reminder) {
		return toStorage(reminder, new ReminderEntity());
	}
	
	public static Reminder fromStorage(ReminderEntity entity) {
		Reminder reminder = new Reminder(entity.content);
		reminder.setId(entity.id);
		reminder.setCreatedAt(entity.createdAt);
		reminder.setUpdatedAt(entity.updatedAt);
		
		return reminder;
	}

	public static ReminderEntity toStorage(Reminder reminder, ReminderEntity entity) {
		entity.content = reminder.getContent();
		entity.updatedAt = reminder.getUpdatedAt();

		if (reminder.getId() != null) {
			entity.id = reminder.getId();
		}
		
		if (reminder.getCreatedAt() != null) {
			entity.createdAt = reminder.getCreatedAt();
		}
		
		return entity;
	}
}
