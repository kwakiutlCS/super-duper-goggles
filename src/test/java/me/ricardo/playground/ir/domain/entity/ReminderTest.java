package me.ricardo.playground.ir.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import me.ricardo.playground.ir.domain.entity.bound.Bound;
import me.ricardo.playground.ir.domain.entity.repetition.NoTime;
import me.ricardo.playground.ir.domain.validation.ReminderUpdate;

class ReminderTest {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    @Test
    void shouldHaveEmptyScheduleFromOffsetIfNoTime() {
        // data
        Reminder reminder = Reminder.Builder.start().withContent("content").build();
        
        // verification
        assertEquals(Optional.empty(), reminder.getTime().schedule(0L, Bound.count(1)).findAny());
    }
    
    @Test
    void shouldNotHaveNullName() {
        // data
        Reminder reminder = Reminder.Builder.start().withUser(null).build();
        
        // verification
        assertFalse(validator.validateProperty(reminder, "user").isEmpty());    
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void shouldNotHaveBlankName(String name) {
        // data
        Reminder reminder = Reminder.Builder.start().withUser(name).build();
        
        // verification
        assertFalse(validator.validateProperty(reminder, "user").isEmpty());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"name", " name  ", "\tname", "\nname"})
    void shouldHaveNonBlankName(String name) {
        // data
        Reminder reminder = Reminder.Builder.start().withUser(name).build();
        
        // verification
        assertTrue(validator.validateProperty(reminder, "user").isEmpty());
    }
    
    @Test
    void shouldNotHaveIdWhenCreating() {
        // data
        Reminder reminder = Reminder.Builder.start().withUser("user").withId(1L).build();
        
        // verification
        assertFalse(validator.validateProperty(reminder, "id").isEmpty());
    }
    
    @Test
    void shouldNotHaveNullIdWhenUpdating() {
        // data
        Reminder reminder = Reminder.Builder.start().withUser("user").build();
        
        // verification
        assertFalse(validator.validateProperty(reminder, "id", ReminderUpdate.class).isEmpty());
    }
    
    @Test
    void shouldNotHavePositiveIdWhenUpdating() {
        // data
        Reminder invalid = Reminder.Builder.start().withUser("user").withId(0L).build();
        Reminder valid = Reminder.Builder.start().withUser("user").withId(1L).build();
            
        // verification
        assertFalse(validator.validateProperty(invalid, "id", ReminderUpdate.class).isEmpty());
        assertTrue(validator.validateProperty(valid, "id", ReminderUpdate.class).isEmpty());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void shouldNotHaveBlankNameWhenUpdating(String name) {
        // data
        Reminder reminder = Reminder.Builder.start().withUser(name).build();
        
        // verification
        assertFalse(validator.validateProperty(reminder, "user", ReminderUpdate.class).isEmpty());
    }
    
    @Test
    void shouldHaveIdBasedEquality() {
        // data
        Reminder reminder1 = Reminder.Builder.start().withId(1L).withUser("user").build();
        Reminder reminder2 = Reminder.Builder.start().withId(1L).build();
        Reminder reminder3 = Reminder.Builder.start().withId(2L).build();
        Reminder reminder4 = Reminder.Builder.start().build();
        Reminder reminder5 = Reminder.Builder.start().build();
        
        // verification
        assertNotEquals(reminder1, null);
        assertEquals(reminder1, reminder2);
        assertNotEquals(reminder1, reminder3);
        assertEquals(reminder4, reminder5);
    }
    
    @Test
    void shouldHaveIdBasedHashCode() {
        // data
        Reminder reminder1 = Reminder.Builder.start().withId(1L).withUser("user").build();
        Reminder reminder2 = Reminder.Builder.start().withId(1L).build();
        Reminder reminder3 = Reminder.Builder.start().withId(2L).build();
        Reminder reminder4 = Reminder.Builder.start().build();
        Reminder reminder5 = Reminder.Builder.start().build();
        
        // verification
        assertEquals(reminder1.hashCode(), reminder2.hashCode());
        assertNotEquals(reminder1.hashCode(), reminder3.hashCode());
        assertEquals(reminder4.hashCode(), reminder5.hashCode());
    }
    
    @Test
    void shouldNotCreateNullTimeReminder() {
        // data
        Reminder reminder = Reminder.Builder.start().build();
        
        // verification
        assertEquals(NoTime.INSTANCE, reminder.getTime());
    }

    @Test
    void shouldNotHaveNullContent() {
        // verification
        assertFalse(validator.validateValue(Reminder.class, "content", null).isEmpty());
    }
}
