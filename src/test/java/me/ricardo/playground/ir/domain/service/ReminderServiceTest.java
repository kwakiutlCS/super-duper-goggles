package me.ricardo.playground.ir.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.entities.Reminder;
import me.ricardo.playground.ir.domain.mocks.ReminderRepositoryFake;

public class ReminderServiceTest {

	private final static long TIMESTAMP = 1000L;
	
	private ReminderService service = new ReminderService(new ReminderRepositoryFake(), Clock.fixed(Instant.ofEpochSecond(TIMESTAMP), ZoneOffset.UTC));
	
	@Test
	public void shouldAddCreateTimeAndUpdateTimeToReminderCreated() {
		// data
		Reminder reminder = new Reminder("content");
		
		// action
		Reminder result = service.createReminder(reminder);
		
		// verification
		assertEquals(TIMESTAMP, result.getCreatedAt());
		assertEquals(TIMESTAMP, result.getUpdatedAt());
	}
}
