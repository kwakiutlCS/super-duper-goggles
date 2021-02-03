package me.ricardo.playground.ir.domain.entity.repetition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.entity.bound.Bound;

class NoTimeTest {

    @Test
    void shouldNotHaveSchedule() {
        assertEquals(0L, NoTime.INSTANCE.schedule(0, Bound.count(3)).count());
    }
    
    @Test
    void shouldNotBeAffectedByTruncation() {
        assertEquals(NoTime.INSTANCE, NoTime.INSTANCE.truncate(0L));
    }
    
    @Test
    void shouldNotHaveExceptions() {
        assertFalse(NoTime.INSTANCE.addException(0L));
    }
}
