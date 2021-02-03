package me.ricardo.playground.ir.domain.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.entity.bound.Bound;
import me.ricardo.playground.ir.domain.entity.repetition.DailyRepetition;
import me.ricardo.playground.ir.domain.entity.repetition.Time;
import me.ricardo.playground.ir.storage.entity.TimeEntity;

class ReminderAdapterTest {

    @Test
    void shouldComputeMinuteOfDayForTimeRemider() {
        // data
        Time time = new DailyRepetition(86400 * 44 + 7320L, 1, Bound.none(), ZoneOffset.UTC);
        
        // action
        TimeEntity result = ReminderAdapter.toStorage(time);
        
        // verification
        assertEquals(7320, result.minute);
    }
}
