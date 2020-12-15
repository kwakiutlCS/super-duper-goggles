package me.ricardo.playground.ir.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ReminderTest {

	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	
	@Test
	void shouldHaveEmptyScheduleIfNoTime() {
		// data
		Reminder reminder = Reminder.Builder.start().withContent("content").build();
		
		// verification
		assertEquals(Optional.empty(), reminder.schedule().findAny());
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
}
