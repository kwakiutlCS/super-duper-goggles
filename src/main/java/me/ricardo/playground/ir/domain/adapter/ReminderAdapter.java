package me.ricardo.playground.ir.domain.adapter;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import me.ricardo.playground.ir.domain.entity.Metadata;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.Bound;
import me.ricardo.playground.ir.domain.entity.repetion.Bound.BoundType;
import me.ricardo.playground.ir.domain.entity.repetion.DailyRepetion;
import me.ricardo.playground.ir.domain.entity.repetion.FixedTime;
import me.ricardo.playground.ir.domain.entity.repetion.NoTime;
import me.ricardo.playground.ir.domain.entity.repetion.Time;
import me.ricardo.playground.ir.domain.operator.Field;
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
		entity.createdAt = metadata.createdAt();
		entity.updatedAt = metadata.updatedAt();
		
		if (entity.time == null) {
		    entity.time = toStorage(reminder.getTime());
		} else {
		    entity.time = toStorage(reminder.getTime(), entity.time);
		}
		
		return entity;
	}
	
	public static TimeEntity toStorage(Time time)  {
	    return toStorage(time, new TimeEntity());
	}
	
	public static TimeEntity toStorage(Time time, TimeEntity entity) {
		if (time instanceof FixedTime f) {
			entity.time = f.getTime();
		} else if (time instanceof DailyRepetion d) {
			entity.time = d.getStart();
			entity.unit = ChronoUnit.DAYS;
			entity.step = d.getStep();
			entity.zone = d.getZone().getId();
			entity.boundType = d.getBound().type().ordinal();
			entity.exceptions = d.getExceptions();
			
			if (d.getBound().type() == BoundType.COUNT_BOUND) {
				entity.boundValue = Long.valueOf(d.getBound().limit());
			} else if (d.getBound().type() == BoundType.TIMESTAMP_BOUND) {
				entity.boundValue = d.getBound().timestamp();
			}
		} else {
		    entity = null;
		}
		
		return entity;
	}
	
	public static Reminder fromStorage(ReminderEntity entity) {
	    return fromStorage(entity, Set.of());
	}
	
	public static Reminder fromStorage(ReminderEntity entity, Set<Field> whitelist) {
		return Reminder.Builder.start()
				               .withContent(entity.content)
				               .withId(entity.id)
				               .withUser(entity.userId)
				               .withMetadata(Metadata.of(entity.createdAt, entity.updatedAt))
				               .withTime(fromStorage(entity.time, whitelist))
				               .build();
	}

	public static Time fromStorage(TimeEntity entity) {
	    return fromStorage(entity, Set.of());
	}
	
	public static Time fromStorage(TimeEntity entity, Set<Field> whitelist) {
		if (entity == null) {
			return NoTime.INSTANCE;
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
		
		// optionally lazy load properties
		Set<Long> exceptions = whitelist.contains(Field.EXCEPTIONS) ? entity.exceptions : Set.of();
		
		return new DailyRepetion(entity.time, entity.step, bound, ZoneId.of(entity.zone), exceptions);
	}
}
