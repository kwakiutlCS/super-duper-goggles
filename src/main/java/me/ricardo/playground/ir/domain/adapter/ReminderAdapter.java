package me.ricardo.playground.ir.domain.adapter;

import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.FixedTime;
import me.ricardo.playground.ir.domain.entity.repetion.Time;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.entity.TimeEntity;

public class ReminderAdapter {
	
	public static ReminderEntity toStorage(Reminder reminder) {
		return toStorage(reminder, new ReminderEntity());
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
		
		if (reminder.getTime() != null) {
			entity.time = toStorage(reminder.getTime());
		}
		
		return entity;
	}
	
	private static TimeEntity toStorage(Time time) {
		TimeEntity entity = new TimeEntity();
		
		if (time instanceof FixedTime f) {
			entity.time = f.getTime();
		}
		
		return entity;
	}
	
	public static Reminder fromStorage(ReminderEntity entity) {
		Reminder reminder = new Reminder(entity.content);
		reminder.setId(entity.id);
		reminder.setCreatedAt(entity.createdAt);
		reminder.setUpdatedAt(entity.updatedAt);
		reminder.setTime(fromStorage(entity.time));
		
		return reminder;
	}

	private static Time fromStorage(TimeEntity entity) {
		if (entity != null) {
			return new FixedTime(entity.time);
		}
		
		return null;
	}
}
