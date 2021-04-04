package me.ricardo.playground.ir.domain.doubles;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import me.ricardo.playground.ir.storage.entity.BoundType;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.entity.TimeEntity;

public class ReminderFakes {
     
    public static ReminderEntity SIMPLE_REMINDER() {
        ReminderEntity reminder = new ReminderEntity();
        reminder.content = "1";
        reminder.userId = "user";
        reminder.createdAt = 0;
        reminder.updatedAt = 0;
        
        return reminder;
    }
    
    public static ReminderEntity FIXED_TIME() {
        ReminderEntity reminder = new ReminderEntity();
        
        TimeEntity timeEntity = new TimeEntity();
        timeEntity.setTimestamp(60L);
        reminder.content = "2";
        reminder.userId = "user";
        reminder.createdAt = 0;
        reminder.updatedAt = 0;
        reminder.time = timeEntity;
        
        return reminder;
    }
    
    public static ReminderEntity DAILY_REPETITION() {
        ReminderEntity reminder = new ReminderEntity();
        
        TimeEntity timeEntity = new TimeEntity();
        timeEntity.setTimestamp(60L);
        timeEntity.step = 1;
        timeEntity.unit = ChronoUnit.DAYS;
        timeEntity.boundType = BoundType.NO_BOUND;
        timeEntity.setZone(ZoneId.of("Z"));
        
        reminder.content = "3";
        reminder.userId = "user";
        reminder.createdAt = 0;
        reminder.updatedAt = 0;
        reminder.time = timeEntity;
        
        return reminder;
    }
    
    public static ReminderEntity DAILY_REPETITION_WITH_EXCEPTIONS() {
        ReminderEntity reminder = DAILY_REPETITION();
        reminder.time.exceptions = Set.of(60L);
        
        return reminder;
    }
    
    public static ReminderEntity DAILY_REPETITION_RECENT() {
        ReminderEntity reminder = DAILY_REPETITION();
        reminder.time.setTimestamp(1610000040L);
        
        return reminder;
    }
}
