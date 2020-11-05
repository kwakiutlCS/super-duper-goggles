package me.ricardo.playground.ir.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;
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

class ReminderResourceTest {

	private final static long TIMESTAMP = 1000L;
	
	private ReminderResource resource;
	
	@BeforeEach
	void init() {
		ReminderEntity entity1 = new ReminderEntity();
		entity1.content = "1";
		entity1.userId = "user";
		entity1.createdAt = 0;
		entity1.updatedAt = 0;
		ReminderEntity entity2 = new ReminderEntity();
		entity2.content = "2";
		entity2.userId = "user";
		entity2.createdAt = 0;
		entity2.updatedAt = 0;
		
		ReminderRepository repository = new ReminderRepositoryFake();
		repository.persist(entity1);
		repository.persist(entity2);
		
		ReminderService service = new ReminderService(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP), ZoneOffset.UTC));
		resource = new ReminderResource(service);
	}
	
	@Test
	void shouldFindAllReminders() {
		assertEquals(2, resource.getReminders("user").size());
	}
	
	@Test
	void shouldNotFindRemindersForDifferentUser() {
		assertEquals(0, resource.getReminders("notTheUser").size());
	}
	
	@Test
	void shouldFindReminderById() {
		assertEquals("1", resource.getReminder("user", 1L).getContent());
	}
	
	@Test
	void shouldNotFindReminderByIdForDifferentUser() {
		assertThrows(NotFoundException.class, () -> resource.getReminder("notTheUser", 1));
	}
	
	@Test
	void shouldThrowNotFoundFindReminderByNonExistingId() {
		assertThrows(NotFoundException.class, () -> resource.getReminder("user", 999));
	}
	
	@Test
	void shouldCreateReminder() {
		// data
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		
		// action
		Response result = resource.createReminder("user", reminder);
		
		// verification
		assertEquals(reminder.getContent(), ((ReminderDto) result.getEntity()).getContent());
		assertEquals("user", ((ReminderDto) result.getEntity()).getUser());
		assertEquals(3, ((ReminderDto) result.getEntity()).getId());
	}
	
	@Test
	void shouldUpdateReminder() {
		// data
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		
		// action
		ReminderDto result = resource.updateReminder("user", 1, reminder);
		
		// verification
		assertEquals(reminder.getContent(), result.getContent());
		assertEquals(1, result.getId());
	}
	
	@Test
	void shouldThrowNotFoundForUpdateReminderOfDifferentUser() {
		// data
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		
		// verification
		assertThrows(NotFoundException.class, () -> resource.updateReminder("notTheUser", 1, reminder));
	}
	
	@Test
	void shouldThrowNotFoundForUpdateInexistentReminder() {
		// data
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		
		// verification
		assertThrows(NotFoundException.class, () -> resource.updateReminder("user", 999, reminder));
	}
	
	@Test
	void shouldDeleteReminder() {
		// action
		Response result = resource.deleteReminder("user", 1);
				
		// verification
		assertEquals(Response.noContent().build().getStatus(), result.getStatus());
	}
	
	@Test
	void shouldThrowExceptionDeleteReminderOtherUser() {
		assertThrows(NotFoundException.class, () -> resource.deleteReminder("notTheUser", 1));
	}
	
	@Test
	void shouldThrowExceptionDeleteNonExistentReminder() {
		assertThrows(NotFoundException.class, () -> resource.deleteReminder("user", 999));
	}
	
	@Test
	void shouldCreateFixedTimeReminder() {
		// data
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		TimeDto time = new TimeDto();
		time.setValue(60L);
		reminder.setTime(time);
		
		// action
		Response result = resource.createReminder("user", reminder);
		
		// verification
		assertEquals(60L, ((ReminderDto) result.getEntity()).getTime().getValue());
	}
	
	@Test
	void shouldCreateDailyRepetionTimeReminder() {
		// data
		BoundDto bound = new BoundDto();
		bound.setLimit(3L);

		TimeDto time = new TimeDto();
		time.setValue(60L);
		time.setStep(1);
		time.setUnit("DAYS");
		time.setZone("UTC");
		time.setBound(bound);
		
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		reminder.setTime(time);
		
		// action
		Response result = resource.createReminder("user", reminder);
		
		// verification
		assertEquals(1, ((ReminderDto) result.getEntity()).getTime().getStep());
		assertEquals("DAYS", ((ReminderDto) result.getEntity()).getTime().getUnit());
		assertEquals("UTC", ((ReminderDto) result.getEntity()).getTime().getZone());
		assertEquals(3, ((ReminderDto) result.getEntity()).getTime().getBound().getLimit());
		assertNull(((ReminderDto) result.getEntity()).getTime().getExceptions());
	}
	
	@Test
	void shouldRetrieveReminderSchedule() {
		// data
		TimeDto time = new TimeDto();
		time.setValue(60L);
		time.setStep(1);
		time.setUnit("DAYS");
		time.setZone("UTC");
		
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		reminder.setTime(time);
		long id = ((ReminderDto) resource.createReminder("user", reminder).getEntity()).getId();
		
		// action
		List<Long> schedule = resource.getSchedule("user", id, 0L, 200000L);
		
		// verification
		assertEquals(60L, schedule.get(0));
		assertEquals(86460L, schedule.get(1));
		assertEquals(172860L, schedule.get(2));
	}
	
	@Test
	void shouldThrowNotFoundExceptionRetrieveReminderScheduleOfDifferentUser() {
		// data
		TimeDto time = new TimeDto();
		time.setValue(60L);
		time.setStep(1);
		time.setUnit("DAYS");
		time.setZone("UTC");
		
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		reminder.setTime(time);
		long id = ((ReminderDto) resource.createReminder("user", reminder).getEntity()).getId();
		
		// verification
	    assertThrows(NotFoundException.class, () -> resource.getSchedule("notTheUser", id, 0L, 200000L));
	}
	
	@Test
	void shouldThrowExceptionIfCalculateScheduleWithEndBeforeStart() {
		assertThrows(ConstraintViolationException.class, () -> resource.getSchedule("user", 1L, 8L, 8L));
	}
	
	@Test
	void shouldAddAndRetrieveExceptionsToDailyReminder() {
		// data
		TimeDto time = new TimeDto();
		time.setValue(60L);
		time.setStep(1);
		time.setUnit("DAYS");
		time.setExceptions(Set.of(60L + ChronoUnit.DAYS.getDuration().getSeconds()));
		
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		reminder.setTime(time);
		
		// action
		Response result = resource.createReminder("user", reminder);
		
		// verification
		assertEquals(86460, ((ReminderDto) result.getEntity()).getTime().getExceptions().toArray(new Long[1])[0]);
	}
	
	@Test
	void shouldThrowExceptionIfExceptionsAreNotScheduled() {
		// data
		TimeDto time = new TimeDto();
		time.setValue(60L);
		time.setStep(1);
		time.setUnit("DAYS");
		time.setExceptions(Set.of(60L + ChronoUnit.DAYS.getDuration().getSeconds(), 9999999999L));
		
		ReminderDto reminder = new ReminderDto();
		reminder.setContent("content");
		reminder.setTime(time);
		
		// verification
		assertThrows(ConstraintViolationException.class, () -> resource.createReminder("user", reminder));
	}
}
