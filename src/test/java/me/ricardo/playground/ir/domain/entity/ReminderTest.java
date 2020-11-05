package me.ricardo.playground.ir.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class ReminderTest {

	@Test
	void shouldHaveEmptyScheduleIfNoTime() {
		// data
		Reminder reminder = Reminder.Builder.start().withContent("content").build();
		
		// verification
		assertEquals(Optional.empty(), reminder.schedule().findAny());
	}
}
