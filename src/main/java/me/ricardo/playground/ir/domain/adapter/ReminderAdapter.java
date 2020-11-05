package me.ricardo.playground.ir.domain.adapter;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import me.ricardo.playground.ir.domain.entity.Metadata;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.Bound;
import me.ricardo.playground.ir.domain.entity.repetion.Bound.BoundType;
import me.ricardo.playground.ir.domain.entity.repetion.DailyRepetion;
import me.ricardo.playground.ir.domain.entity.repetion.FixedTime;
import me.ricardo.playground.ir.domain.entity.repetion.Time;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.entity.TimeEntity;

public class ReminderAdapter {
	
	private ReminderAdapter() { }
	
	public static ReminderEntity toStorage(Reminder reminder, Metadata metadata) {
		return toStorage(reminder, metadata, new ReminderEntity());
	}
	
	public static ReminderEntity toStorage(Reminder reminder, Metadata metadata, ReminderEntity entity) {
		entity.content = reminder.getContent();
		entity.userId = reminder.getUser();
		entity.createdAt = metadata.getCreatedAt();
		entity.updatedAt = metadata.getUpdatedAt();

		if (reminder.getId() != null) {
			entity.id = reminder.getId();
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
			entity.exceptions = d.getExceptions();
			
			if (d.getBound().getType() == BoundType.COUNT_BOUND) {
				entity.boundValue = Long.valueOf(d.getBound().getLimit());
			} else if (d.getBound().getType() == BoundType.TIMESTAMP_BOUND) {
				entity.boundValue = d.getBound().getTimestamp();
			}
		}
		
		return entity;
	}
	
	public static Reminder fromStorage(ReminderEntity entity) {
		return Reminder.Builder.start()
				               .withContent(entity.content)
				               .withId(entity.id)
				               .withUser(entity.userId)
				               .withMetadata(new Metadata(entity.createdAt, entity.updatedAt))
				               .withTime(fromStorage(entity.time))
				               .build();
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
		
		return new DailyRepetion(entity.time, entity.step, bound, ZoneId.of(entity.zone), entity.exceptions);
	}
}
