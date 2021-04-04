package me.ricardo.playground.ir.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.entity.TimeEntity;

@QuarkusTest
@QuarkusTestResource(value = PostgresqlResource.class)
@DBRider
class ReminderServiceIT {
    
    @Inject
    ReminderService service;
    
    @Test
    @DataSet(value = "dataset/daily_repetition_reminder_with_exception.yml", cleanBefore = true)
    void shouldAccountForExceptionsWhenScheduling() {
        // action
        List<Long> schedule = service.getSchedule(1L, "user", 0, Bound.count(3));
        
        // verification
        assertEquals(List.of(3600L, 176400L), schedule);
    }
    
    @Test
    @DataSet(value = "dataset/daily_repetition_reminder.yml", cleanBefore = true)
    @ExpectedDataSet(value = "expected/daily_repetition_reminder_with_exception.yml")
    void shouldAddException() {
        // action
        boolean result = service.addException(1L, "user", 3600L);
        
        // verification
        assertTrue(result);
    }
    
    @Test
    @DataSet(value = "dataset/daily_repetition_reminder.yml", cleanBefore = true)
    @ExpectedDataSet(value = "expected/daily_repetition_reminder_truncated.yml")
    void shouldTruncateReminder() {
        // action
        Optional<Reminder> result = service.truncate(1L, "user", 3601);
        
        // verification
        assertEquals(List.of(3600L), result.get().getTime().schedule(0L, Bound.count(2)).collect(Collectors.toList()));
    }
    
    @Test
    @DataSet(value = "dataset/daily_repetition_reminder.yml", cleanBefore = true)
    void shouldDeleteTruncateReminderIfNoSchedulingRemains() {
        // action
        Optional<Reminder> result = service.truncate(1L, "user", 0L);
        
        // verification
        assertEquals(Optional.empty(), result);
        assertEquals(0, TimeEntity.count());
        assertEquals(0, ReminderEntity.count());
    }
}
