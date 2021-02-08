package me.ricardo.playground.ir.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.doubles.ReminderFakes;
import me.ricardo.playground.ir.domain.doubles.ReminderRepositoryFake;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

class ReminderQuerierTest {

    private ReminderRepository repository = new ReminderRepositoryFake(ReminderFakes.FIXED_TIME(), ReminderFakes.FIXED_TIME(),
            ReminderFakes.DAILY_REPETITION(), ReminderFakes.DAILY_REPETITION_WITH_EXCEPTIONS());
    
    private ReminderQuerier querier = new ReminderQuerier(repository);
    
    @Test
    void shouldBeAbleToFindFixedTimeReminders() {
        assertEquals(2, querier.findAtTimestamp(60L).size());
        assertEquals(0, querier.findAtTimestamp(120L).size());
    }
    
    @Test
    void shouldBeAbleToFindRecurrentReminders() {
        assertEquals(1, querier.findRecurrentAtTimestamp(60L).size());
        assertEquals(2, querier.findRecurrentAtTimestamp(60L + 3 * 86400).size());
        assertEquals(0, querier.findRecurrentAtTimestamp(120L).size());
    }
    
    @Test
    void shouldBeAbleToFindRecurrentRemindersAfterDayLightSavingsChangePortugal() {
        // data
        ReminderEntity reminder = ReminderFakes.DAILY_REPETITION();
        reminder.time.setTimestamp(1610000040L);
        reminder.time.setZone(ZoneId.of("Europe/Lisbon"));
        ReminderQuerier querier = new ReminderQuerier(new ReminderRepositoryFake(reminder));
        
        // verification
        long timestampBefore = 1615011240L;
        long timestampAfter = 1628399640L;
        assertEquals(1, querier.findRecurrentAtTimestamp(timestampBefore).size());
        assertEquals(1, querier.findRecurrentAtTimestamp(timestampAfter).size());
    }
    
    @Test
    void shouldBeAbleToFindRecurrentRemindersAfterDayLightSavingsChangeGermany() {
        // data
        ReminderEntity reminder = ReminderFakes.DAILY_REPETITION();
        reminder.time.setTimestamp(1610000040L);
        reminder.time.setZone(ZoneId.of("Europe/Berlin"));
        ReminderQuerier querier = new ReminderQuerier(new ReminderRepositoryFake(reminder));
        
        // verification
        long timestampBefore = 1615011240L;
        long timestampAfter = 1628399640L;
        assertEquals(1, querier.findRecurrentAtTimestamp(timestampBefore).size());
        assertEquals(1, querier.findRecurrentAtTimestamp(timestampAfter).size());
    }
    
    @Test
    void shouldBeAbleToFilterRemindersScheduleAtSameMinute() {
        // data
        ReminderEntity reminder1 = ReminderFakes.DAILY_REPETITION();
        reminder1.time.setTimestamp(1610000040L);
        reminder1.time.setZone(ZoneId.of("Europe/Lisbon"));
        
        ReminderEntity reminder2 = ReminderFakes.DAILY_REPETITION();
        reminder2.time.setTimestamp(1610000040L);
        reminder2.time.setZone(ZoneId.of("Europe/Lisbon"));
        reminder2.time.step = Integer.MAX_VALUE;
        ReminderQuerier querier = new ReminderQuerier(new ReminderRepositoryFake(reminder1, reminder2));
        
        // verification
        long timestampBefore = 1615011240L;
        long timestampAfter = 1628399640L;
        assertEquals(1, querier.findRecurrentAtTimestamp(timestampBefore).size());
        assertEquals(1, querier.findRecurrentAtTimestamp(timestampAfter).size());
    }
}
