package me.ricardo.playground.ir.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.mocks.ReminderRepositoryFake;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

public class ReminderServiceTest {

	private final static long TIMESTAMP = 1000L;
	
	private ReminderRepository repository = new ReminderRepositoryFake();
	
	private ReminderService service = new ReminderService(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP), ZoneOffset.UTC));
	
	@Test
	public void shouldAddCreateTimeAndUpdateTimeToReminderCreated() {
		// data
		Reminder reminder = Reminder.Builder.start().withContent("content").build();
		
		// action
		Reminder result = service.createReminder(reminder);
		
		// verification
		assertEquals(TIMESTAMP, result.getMetadata().getCreatedAt());
		assertEquals(TIMESTAMP, result.getMetadata().getUpdatedAt());
	}
	
	@Test
	public void shouldUpdateReminderContent() {
		// data
		Reminder reminder = Reminder.Builder.start().withContent("content").build();
		long id = service.createReminder(reminder).getId();
		
		// action
		ReminderService svc2 = new ReminderService(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP + 1), ZoneOffset.UTC));
		Reminder result = svc2.updateReminder(id, Reminder.Builder.start().withContent("updated").build());
		
		// verification
		assertEquals(TIMESTAMP, result.getMetadata().getCreatedAt());
		assertEquals(TIMESTAMP + 1, result.getMetadata().getUpdatedAt());
		assertEquals("updated", result.getContent());
	}
	
	@Test
	public void shouldDeleteReminder() {
		// data
		Reminder reminder = Reminder.Builder.start().withContent("original").build();
		long id = service.createReminder(reminder).getId();
		
		// action
		service.deleteReminder(id);
		
		// verification
		assertThrows(NotFoundException.class, () -> service.getReminder(id));
	}
}
