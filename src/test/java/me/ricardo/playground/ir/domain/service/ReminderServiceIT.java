package me.ricardo.playground.ir.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.entity.TimeEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

@QuarkusTest
@QuarkusTestResource(value = PostgresqlResource.class)
public class ReminderServiceIT {

	private static final String DEFAULT_USER = "default_user";
	
	@Inject
	ReminderService service;
	
	@Inject
	ReminderRepository repository;
	
	@BeforeAll
	@Transactional
	static void init() {
		ReminderEntity reminder1 = new ReminderEntity();
		reminder1.content = "reminder1";
		reminder1.userId = DEFAULT_USER;
		
		ReminderEntity reminder2 = new ReminderEntity();
		reminder2.content = "reminder1";
		reminder2.userId = DEFAULT_USER;
	
        reminder1.persist();
        reminder2.persist();
	}
	
	@Test
	@Transactional
	void shouldCreateReminder() {
		// data
		long count = repository.count();
		Reminder reminder = Reminder.Builder.start().withUser("creatingUser").build();
		
		// action
		Reminder result = service.createReminder(reminder);
		
		// verification
		long id = repository.listAll().stream().mapToLong(e -> e.id).max().getAsLong();
		assertEquals(count + 1, ReminderEntity.count());
		assertEquals(result.getId(), id);
	}
	
	@Test
	void shouldFindRemindersByUser() {
		// action
		List<Reminder> userReminders = service.getReminders(DEFAULT_USER);
		List<Reminder> otherUserReminders = service.getReminders("otherUser");
		
		// verification
		assertEquals(2, userReminders.size());
		assertEquals(0, otherUserReminders.size());
	}
	
	@Test
	void shouldFindReminderById() {
		// action
		Optional<Reminder> userReminder = service.getReminder(1L, DEFAULT_USER);
		Optional<Reminder> otherUserReminder = service.getReminder(1L, "otherUser");
		
		// verification
		assertEquals(1L, userReminder.get().getId());
		assertEquals(Optional.empty(), otherUserReminder);
	}
	
	@Test
	void shouldUpdateReminder() {
		// data
		Reminder reminder = Reminder.Builder.start().withContent("updated").withId(1L).withUser(DEFAULT_USER).build();
		
		//action
		service.updateReminder(reminder);
		
		// verification
		assertEquals("updated", repository.findById(1L).content);
	}
	
	@Test
	@Transactional
	void shouldDeleteReminder() {
		// data
		ReminderEntity reminder = new ReminderEntity();
		reminder.userId = "deletionUser";
		reminder.persist();
		long count = ReminderEntity.count();
		
		// action
		service.deleteReminder(reminder.id, "deletionUser");
		
		// verification
		assertEquals(count-1, ReminderEntity.count());
	}
	
	@Test
	@Transactional
	void shouldRemoveOrphanEntities() {
		// data
		long reminderCount = ReminderEntity.count();
		long timeCount = TimeEntity.count();
		
		TimeEntity time = new TimeEntity();
		time.unit = ChronoUnit.DAYS;
		time.step = 1;
		time.time = 0;
		
		ReminderEntity reminder = new ReminderEntity();
		reminder.userId = "deletionUser";
		reminder.time = time;
		
		reminder.persist();
		assertEquals(timeCount + 1, TimeEntity.count());
		assertEquals(reminderCount + 1, ReminderEntity.count());
		
		// action
		service.deleteReminder(reminder.id, "deletionUser");
		
		// verification
		assertEquals(timeCount, TimeEntity.count());
	}
}
