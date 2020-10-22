package me.ricardo.playground.ir.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class ReminderTest {

	@Test
	public void shouldHaveEmptyScheduleIfNoTime() {
		// data
		Reminder reminder = new Reminder("content");
		
		// verification
		assertEquals(Optional.empty(), reminder.schedule().findAny());
	}
}
