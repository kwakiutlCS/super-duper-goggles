package me.ricardo.playground.ir.domain.doubles;

import java.time.temporal.ChronoUnit;

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
        timeEntity.time = 60L;
        reminder.content = "2";
        reminder.userId = "user";
        reminder.createdAt = 0;
        reminder.updatedAt = 0;
        reminder.time = timeEntity;
        
        return reminder;
    }
    
    public static ReminderEntity DAILY_REPETION() {
        ReminderEntity reminder = new ReminderEntity();
        
        TimeEntity timeEntity = new TimeEntity();
        timeEntity.time = 60L;
        timeEntity.step = 1;
        timeEntity.unit = ChronoUnit.DAYS;
        timeEntity.boundType = 0;
        timeEntity.boundValue = 1L;
        timeEntity.zone = "Z";
        
        reminder.content = "3";
        reminder.userId = "user";
        reminder.createdAt = 0;
        reminder.updatedAt = 0;
        reminder.time = timeEntity;
        
        return reminder;
    }
}
