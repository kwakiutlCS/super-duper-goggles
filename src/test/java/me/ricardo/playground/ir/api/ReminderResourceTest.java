package me.ricardo.playground.ir.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.api.entity.BoundDto;
import me.ricardo.playground.ir.api.entity.ReminderDto;
import me.ricardo.playground.ir.api.entity.TimeDto;
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
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		
		// action
		Response result = resource.createReminder(reminder);
		
		// verification
		assertEquals(reminder.getContent(), ((ReminderDto) result.getEntity()).getContent());
		assertEquals(3, ((ReminderDto) result.getEntity()).getId());
	}
	
	@Test
	public void shouldUpdateReminder() {
		// data
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		
		// action
		ReminderDto result = resource.updateReminder(1, reminder);
		
		// verification
		assertEquals(reminder.getContent(), result.getContent());
		assertEquals(1, result.getId());
	}
	
	@Test
	public void shouldThrowNotFoundForUpdateInexistentReminder() {
		// data
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		
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
	public void shouldCreateFixedTimeReminder() {
		// data
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		TimeDto time = new TimeDto();
		time.setValue(60L);
		reminder.setTime(time);
		
		// action
		Response result = resource.createReminder(reminder);
		
		// verification
		assertEquals(60L, ((ReminderDto) result.getEntity()).getTime().getValue());
	}
	
	@Test
	public void shouldCreateDailyRepetionTimeReminder() {
		// data
		BoundDto bound = new BoundDto();
		bound.setLimit(3L);

		TimeDto time = new TimeDto();
		time.setValue(60L);
		time.setStep(1);
		time.setUnit(ChronoUnit.DAYS);
		time.setZone("UTC");
		time.setBound(bound);
		
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		reminder.setTime(time);
		
		// action
		Response result = resource.createReminder(reminder);
		
		// verification
		assertEquals(1, ((ReminderDto) result.getEntity()).getTime().getStep());
		assertEquals(ChronoUnit.DAYS, ((ReminderDto) result.getEntity()).getTime().getUnit());
		assertEquals("UTC", ((ReminderDto) result.getEntity()).getTime().getZone());
		assertEquals(3, ((ReminderDto) result.getEntity()).getTime().getBound().getLimit());
	}
}
