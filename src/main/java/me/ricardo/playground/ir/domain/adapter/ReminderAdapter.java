package me.ricardo.playground.ir.domain.adapter;

import java.time.temporal.ChronoUnit;
import java.util.Set;

import me.ricardo.playground.ir.domain.entity.Metadata;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.bound.AtomicBound;
import me.ricardo.playground.ir.domain.entity.bound.Bound;
import me.ricardo.playground.ir.domain.entity.bound.CountBound;
import me.ricardo.playground.ir.domain.entity.bound.TimeBound;
import me.ricardo.playground.ir.domain.entity.repetition.DailyRepetition;
import me.ricardo.playground.ir.domain.entity.repetition.FixedTime;
import me.ricardo.playground.ir.domain.entity.repetition.NoTime;
import me.ricardo.playground.ir.domain.entity.repetition.Time;
import me.ricardo.playground.ir.domain.operator.Field;
import me.ricardo.playground.ir.storage.entity.BoundType;
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
            entity.setTimestamp(f.getTime());
            
        } else if (time instanceof DailyRepetition d) {
            entity.setTimestamp(d.getStart());
            entity.unit = ChronoUnit.DAYS;
            entity.step = d.getStep();
            entity.setZone(d.getZone());
            entity.exceptions = d.getExceptions();
            entity.boundValue = d.getBound().getValue();
            entity.boundType = toStorage(d.getBound());

        } else {
            entity = null;
        }
        
        return entity;
    }
    
    private static BoundType toStorage(AtomicBound bound) {
        if (bound instanceof TimeBound) {
            return BoundType.TIME_BOUND;
        } else if (bound instanceof CountBound) {
            return BoundType.COUNT_BOUND;
        } else {
            return BoundType.NO_BOUND;
        }
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
            return new FixedTime(entity.getTimestamp());
        }
        
        AtomicBound bound = fromStorage(entity.boundType, entity.boundValue);
        
        // optionally lazy load properties
        Set<Long> exceptions = whitelist.contains(Field.EXCEPTIONS) ? entity.exceptions : Set.of();
        
        return new DailyRepetition(entity.getTimestamp(), entity.step, bound, entity.getZone(), exceptions);
    }
    
    private static AtomicBound fromStorage(BoundType bound, Long boundValue) {
        switch (bound) {
        case COUNT_BOUND:
            return Bound.count(boundValue);
        case TIME_BOUND:
            return Bound.timestamp(boundValue);
        case NO_BOUND:
        default:
            return Bound.none();
        }
    }
}
