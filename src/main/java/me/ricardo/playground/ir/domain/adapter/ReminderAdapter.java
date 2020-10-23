package me.ricardo.playground.ir.domain.adapter;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.Bound;
import me.ricardo.playground.ir.domain.entity.repetion.Bound.BoundType;
import me.ricardo.playground.ir.domain.entity.repetion.DailyRepetion;
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
		} else if (time instanceof DailyRepetion d) {
			entity.time = d.getStart();
			entity.unit = ChronoUnit.DAYS;
			entity.step = d.getStep();
			entity.zone = d.getZone().getId();
			entity.boundType = d.getBound().getType().ordinal();
			
			if (d.getBound().getType() == BoundType.COUNT_BOUND) {
				entity.boundValue = Long.valueOf(d.getBound().getLimit());
			} else if (d.getBound().getType() == BoundType.TIMESTAMP_BOUND) {
				entity.boundValue = d.getBound().getTimestamp();
			}
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
		if (entity == null) {
			return null;
		}
		
		if (entity.unit == null) {
			return new FixedTime(entity.time);
		}
		
		Bound bound = Bound.none();
		if (entity.boundType == BoundType.COUNT_BOUND.ordinal()) {
			bound = Bound.count(entity.boundValue);
		} else if (entity.boundType == BoundType.TIMESTAMP_BOUND.ordinal()) {
			bound = Bound.timestamp(entity.boundValue);
		}
		
		return new DailyRepetion(entity.time, entity.step, bound, ZoneId.of(entity.zone));
	}
}
