package me.ricardo.playground.ir.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

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
class ReminderQuerierIT {

    @Inject
    ReminderQuerier querier;
    
    @Inject
    ReminderCrud crud;
    
    @Test
    @DataSet(value = "dataset/fixed_time_reminder.yml", cleanBefore = true)
    void shouldFindReminderAtTimestamp() {
        assertEquals(1, querier.findAtTimestamp(3600L).size());
        assertEquals(0, querier.findAtTimestamp(3601L).size());
    }
    
    @Test
    @DataSet(value = "dataset/daily_repetition_multi_timezone.yml", cleanBefore = true)
    void shouldFindRecurrentReminderDifferentDaylightSavings() {
        assertEquals(1, querier.findRecurrentAtTimestamp(1609504200).size());
        assertEquals(1, querier.findRecurrentAtTimestamp(1622287800).size());
        assertEquals(2, querier.findRecurrentAtTimestamp(1629113400).size());
        assertEquals(2, querier.findRecurrentAtTimestamp(1640089800).size());

        assertEquals(1, querier.findRecurrentAtTimestamp(1613907000).size());
        assertEquals(2, querier.findRecurrentAtTimestamp(1630492200).size());
        assertEquals(2, querier.findRecurrentAtTimestamp(1641123000).size());
    }
    
    @Test
    @DataSet(cleanBefore = true)
    void shouldCreateAndFindRecurrent() {
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
        assertEquals(1, querier.findRecurrentAtTimestamp(1609504200).size());
        assertEquals(1, querier.findRecurrentAtTimestamp(1622287800).size());
        assertEquals(2, querier.findRecurrentAtTimestamp(1629113400).size());
        assertEquals(2, querier.findRecurrentAtTimestamp(1640089800).size());

        assertEquals(1, querier.findRecurrentAtTimestamp(1613907000).size());
        assertEquals(2, querier.findRecurrentAtTimestamp(1630492200).size());
        assertEquals(2, querier.findRecurrentAtTimestamp(1641123000).size());
    }
}
