package me.ricardo.playground.ir.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.api.entity.ReminderDto;
import me.ricardo.playground.ir.api.entity.TimeDto;
import me.ricardo.playground.ir.domain.entity.repetion.FixedTime;
import me.ricardo.playground.ir.domain.mocks.ReminderRepositoryFake;
import me.ricardo.playground.ir.domain.service.ReminderService;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

public class ReminderResourceTest {

	private final static long TIMESTAMP = 1000L;
	
	private ReminderResource resource;
	
	@BeforeEach
	public void init() {
		ReminderEntity entity1 = new ReminderEntity();
		entity1.content = "1";
		entity1.createdAt = 0;
		entity1.updatedAt = 0;
		ReminderEntity entity2 = new ReminderEntity();
		entity2.content = "2";
		entity2.createdAt = 0;
		entity2.updatedAt = 0;
		
		ReminderRepository repository = new ReminderRepositoryFake();
		repository.persist(entity1);
		repository.persist(entity2);
		
		ReminderService service = new ReminderService(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP), ZoneOffset.UTC));
		resource = new ReminderResource(service);
	}
	
	@Test
	public void shouldFindAllReminders() {
		assertEquals(2, resource.getReminders().size());
	}
	
	@Test
	public void shouldFindReminderById() {
		assertEquals("1", resource.getReminder(1L).getContent());
	}
	
	@Test
	public void shouldThrowNotFoundFindReminderByNonExistingId() {
		assertThrows(NotFoundException.class, () -> resource.getReminder(999));
	}
	
	@Test
	public void shouldCreateReminder() {
		// data
		ReminderDto reminder = new ReminderDto("content");
		
		// action
		Response result = resource.createReminder(reminder);
		
		// verification
		assertEquals(reminder.getContent(), ((ReminderDto) result.getEntity()).getContent());
		assertEquals(3, ((ReminderDto) result.getEntity()).getId());
	}
	
	@Test
	public void shouldUpdateReminder() {
		// data
		ReminderDto reminder = new ReminderDto("updated");
		
		// action
		ReminderDto result = resource.updateReminder(1, reminder);
		
		// verification
		assertEquals(reminder.getContent(), result.getContent());
		assertEquals(1, result.getId());
	}
	
	@Test
	public void shouldThrowNotFoundForUpdateInexistentReminder() {
		// data
		ReminderDto reminder = new ReminderDto("updated");
		
		// verification
		assertThrows(NotFoundException.class, () -> resource.updateReminder(999, reminder));
	}
	
	@Test
	public void shouldDeleteReminder() {
		// action
		Response result = resource.deleteReminder(1);
				
		// verification
		assertEquals(Response.noContent().build().getStatus(), result.getStatus());
	}
	
	@Test
	public void shouldThrowExceptionDeleteNonExistentReminder() {
		assertThrows(NotFoundException.class, () -> resource.deleteReminder(999));
	}
	
	@Test
	public void shouldCreateTimeReminder() {
		// data
		ReminderDto reminder = new ReminderDto("content");
		reminder.setTime(new TimeDto(60L));
		
		// action
		Response result = resource.createReminder(reminder);
		
		// verification
		assertEquals(60L, ((ReminderDto) result.getEntity()).getTime().getValue());
	}
}
