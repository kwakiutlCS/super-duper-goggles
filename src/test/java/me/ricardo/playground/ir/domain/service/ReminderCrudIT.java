package me.ricardo.playground.ir.domain.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.database.rider.cdi.api.DBRider;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.bound.Bound;
import me.ricardo.playground.ir.domain.entity.repetition.DailyRepetition;
import me.ricardo.playground.ir.domain.entity.repetition.FixedTime;
import me.ricardo.playground.ir.domain.entity.repetition.NoTime;
import me.ricardo.playground.ir.domain.entity.repetition.Time;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.entity.TimeEntity;

@QuarkusTest
@QuarkusTestResource(value = PostgresqlResource.class)
@DBRider
class ReminderCrudIT {

    @Inject
    ReminderCrud crud;
        
    @Test
    @DataSet(cleanBefore = true)
    @ExpectedDataSet(value = "expected/simple_reminder.yml")
    void shouldCreateReminder() {
        // data
        Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").build();
        
        // action
        Reminder result = crud.createReminder(reminder);
        
        // verification
        assertNotNull(result.getId());
    }
    
    @Test
    @DataSet(cleanBefore = true)
    @ExpectedDataSet(value = "expected/daily_repetition_reminder.yml")
    void shouldCreateTimeReminder() {
        // data
        Time time = new DailyRepetition(90060L);
        Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();
        
        // action
        Reminder result = crud.createReminder(reminder);
        
        // verification
        assertEquals("content", result.getContent());
    }
    
    @Test
    @DataSet(value = "dataset/simple_reminders.yml", cleanBefore = true)
    void shouldFindRemindersByUser() {
        // action
        List<Reminder> userReminders = crud.getReminders("user");
        List<Reminder> otherUserReminders = crud.getReminders("otherUser");
        
        // verification
        assertEquals(2, userReminders.size());
        assertEquals(0, otherUserReminders.size());
    }
    
    @Test
    @DataSet(value = "dataset/daily_repetition_reminder_with_exception.yml", cleanBefore = true)
    void shouldFindTimeRemindersByUser() {
        // action
        List<Reminder> userReminders = crud.getReminders("user");
        List<Reminder> otherUserReminders = crud.getReminders("otherUser");
        
        // verification
        assertEquals(2, userReminders.size());
        assertEquals(0, otherUserReminders.size());
    }
    
    @Test
    @DataSet(value = "dataset/simple_reminders.yml", cleanBefore = true)
    void shouldFindReminderById() {
        // action
        Optional<Reminder> userReminder = crud.getReminder(1L, "user");
        Optional<Reminder> otherUserReminder = crud.getReminder(1L, "otherUser");
        
        // verification
        assertEquals(1L, userReminder.get().getId());
        assertEquals(Optional.empty(), otherUserReminder);
    }
    
    @Test
    @DataSet(value = "dataset/simple_reminders.yml", cleanBefore = true)
    @ExpectedDataSet(value = "expected/updated_reminders.yml")
    void shouldUpdateReminder() {
        // data
        Reminder reminder = Reminder.Builder.start().withContent("updated").withId(1L).withUser("user").build();
        
        //action
        Optional<Reminder> updated = crud.updateReminder(reminder);
        
        // verification
        assertEquals("updated", updated.get().getContent());
    }
    
    @Test
    @DataSet(value = "dataset/fixed_time_reminder.yml", cleanBefore = true)
    @ExpectedDataSet(value = "expected/simple_reminder.yml")
    void shouldRemoveOrphanEntitiesWhenUpdating() {
        // data
        Reminder reminder = Reminder.Builder.start().withContent("content").withId(1L).withUser("user").build();
        
        //action
        Reminder result = crud.updateReminder(reminder).get();
        
        // verification
        assertEquals(NoTime.INSTANCE, result.getTime());
        assertEquals(1, ReminderEntity.count());
        assertEquals(0, TimeEntity.count());
    }
    
    @Test
    @DataSet(value = "dataset/daily_repetition_reminder.yml", cleanBefore = true)
    @ExpectedDataSet(value = "expected/fixed_time_reminder.yml")
    void shouldUpdateChildEntity() {
        // data
        Reminder reminder = Reminder.Builder.start().withContent("content").withId(1L).withTime(new FixedTime(7200L)).withUser("user").build();
        
        //action
        Reminder result = crud.updateReminder(reminder).get();
        
        // verification
        assertNotEquals(NoTime.INSTANCE, result.getTime());
    }
    
    @Test
    @DataSet(value = "dataset/simple_reminders.yml", cleanBefore = true)
    @ExpectedDataSet(value = "expected/deleted_reminders.yml")
    void shouldDeleteReminder() {
        assertEquals(2, ReminderEntity.count());
        
        // action
        boolean result = crud.deleteReminder(1L, "user");
        
        // verification
        assertTrue(result);
        assertEquals(1, ReminderEntity.count());
    }
    
    @Test
    @DataSet(value = "dataset/fixed_time_reminder.yml", cleanBefore = true)
    void shouldRemoveOrphanEntitiesWhenDeleting() {
        // action
        boolean result = crud.deleteReminder(1L, "user");
        
        // verification
        assertTrue(result);
        assertEquals(0, ReminderEntity.count());
        assertEquals(0, TimeEntity.count());
    }
    
    @Test
    @DataSet(cleanBefore = true)
    @ExpectedDataSet(value = "expected/daily_repetition_multi_timezone.yml")
    void shouldCreateRemindersWithMinuteInformation() {
        // data
        // west europe reminder, 12h30 January 1st -> expected minute (12*60+30)*60 seconds (same offset as UTC in non daylight savings time)
        Reminder reminder1 = Reminder.Builder.start().withContent("westeurope").withUser("user").withTime(new DailyRepetition(1609504200L, 1, Bound.none(), ZoneId.of("Europe/Lisbon"))).build();
        // west europe reminder, 12h30 July 1st -> expected minute (12*60+30)*60 seconds (same offset as UTC in non daylight savings time)
        Reminder reminder2 = Reminder.Builder.start().withContent("westeuropesummer").withUser("user").withTime(new DailyRepetition(1625139000L, 1, Bound.none(), ZoneId.of("Europe/Lisbon"))).build();
        // central europe reminder, 12h30 January 1st -> expected minute (11*60+30)*60 seconds (+1 offset than UTC in non daylight savings time, so UTC is 1 hour earlier, 11)
        Reminder reminder3 = Reminder.Builder.start().withContent("centraleurope").withUser("user").withTime(new DailyRepetition(1609500600L, 1, Bound.none(), ZoneId.of("Europe/Berlin"))).build();
        // central europe reminder, 12h30 July 1st -> expected minute (11*60+30)*60 seconds (+1 offset than UTC in non daylight savings time, so UTC is 1 hour earlier, 11)
        Reminder reminder4 = Reminder.Builder.start().withContent("centraleuropesummer").withUser("user").withTime(new DailyRepetition(1625135400L, 1, Bound.none(), ZoneId.of("Europe/Berlin"))).build();
         
        // action
        Reminder result1 = crud.createReminder(reminder1);
        Reminder result2 = crud.createReminder(reminder2);
        Reminder result3 = crud.createReminder(reminder3);
        Reminder result4 = crud.createReminder(reminder4);
        
        // verification
        assertNotNull(result1.getId());
        assertNotNull(result2.getId());
        assertNotNull(result3.getId());
        assertNotNull(result4.getId());
    }
}
