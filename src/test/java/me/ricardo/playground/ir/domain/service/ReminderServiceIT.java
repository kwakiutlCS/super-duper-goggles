package me.ricardo.playground.ir.domain.service;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.FixedTime;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.entity.TimeEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

@QuarkusTest
@QuarkusTestResource(value = PostgresqlResource.class)
class ReminderServiceIT {

	private static final String DEFAULT_USER = "default_user";
	
	@Inject
	ReminderService service;
	
	@Inject
	ReminderRepository repository;
	
	@Inject
	TransactionManager tm;
	
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
        
        TimeEntity time1 = new TimeEntity();
		time1.unit = ChronoUnit.DAYS;
		time1.step = 1;
		time1.time = 0;
		time1.boundType = 1;
		time1.boundValue = 1L;
		time1.zone = "Z";
		
		ReminderEntity timeReminder1 = new ReminderEntity();
		timeReminder1.userId = DEFAULT_USER;
		timeReminder1.time = time1;
		
		TimeEntity time2 = new TimeEntity();
        time2.unit = ChronoUnit.DAYS;
        time2.step = 1;
        time2.time = 0;
        time2.boundType = 1;
        time2.boundValue = 1L;
        time2.zone = "Z";
        
        ReminderEntity timeReminder2 = new ReminderEntity();
        timeReminder2.userId = DEFAULT_USER;
        timeReminder2.time = time2;
        
        TimeEntity time3 = new TimeEntity();
        time3.unit = ChronoUnit.DAYS;
        time3.step = 1;
        time3.time = 0;
        time3.boundType = 1;
        time3.boundValue = 1L;
        time3.zone = "Z";
        
        ReminderEntity timeReminder3 = new ReminderEntity();
        timeReminder3.userId = DEFAULT_USER;
        timeReminder3.time = time3;
        
        timeReminder1.persist();
		timeReminder2.persist();
		timeReminder3.persist();
	}
	
	@Test
	void shouldCreateReminder() {
		// data
		long count = repository.count();
		Reminder reminder = Reminder.Builder.start().withUser(DEFAULT_USER).build();
		
		// action
		Reminder result = service.createReminder(reminder);
		
		// verification
		long id = repository.listAll().stream().mapToLong(e -> e.id).max().getAsLong();
		assertEquals(count + 1, ReminderEntity.count());
		assertEquals(result.getId(), id);
	}
	
	@Test
	void shouldFindRemindersByUser() {
		// data
		long count = ReminderEntity.count();
		
		// action
		List<Reminder> userReminders = service.getReminders(DEFAULT_USER);
		List<Reminder> otherUserReminders = service.getReminders("otherUser");
		
		// verification
		assertEquals(count, userReminders.size());
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
	void shouldUpdateReminder() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		// data
		Reminder reminder = Reminder.Builder.start().withContent("updated").withId(1L).withUser(DEFAULT_USER).build();
		
		//action
		service.updateReminder(reminder);
		
		// verification
		tm.begin();
		assertEquals("updated", repository.findById(1L).content);
		tm.commit();
	}
	
	@Test
    void shouldRemoveOrphanEntitiesWhenUpdating() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        // data
	    long timeCount = TimeEntity.count();
        long id = repository.listAll().stream().filter(r -> r.time != null).mapToLong(e -> e.id).min().getAsLong();
        Reminder reminder = Reminder.Builder.start().withContent("updated").withId(id).withUser(DEFAULT_USER).build();
        
        //action
        Reminder result = service.updateReminder(reminder).get();
        
        // verification
        tm.begin();
        assertNull(result.getTime());
        assertEquals(timeCount-1, TimeEntity.count());
        tm.commit();
    }
	
	@Test
    void shouldUpdateChildEntity() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        // data
        long id = repository.listAll().stream().filter(r -> r.time != null).mapToLong(e -> e.id).max().getAsLong();
        long childId = repository.findById(id).time.id;
        Reminder reminder = Reminder.Builder.start().withContent("updated").withId(id).withTime(new FixedTime(4L)).withUser(DEFAULT_USER).build();
        
        //action
        Reminder result = service.updateReminder(reminder).get();
        
        // verification
        tm.begin();
        assertNotNull(result.getTime());
        assertEquals(childId, repository.findById(id).time.id);
        tm.commit();
    }
	
	@Test
	void shouldDeleteReminder() {
		// data
		long id = repository.listAll().stream().filter(r -> r.time == null).mapToLong(e -> e.id).max().getAsLong();
		long count = ReminderEntity.count();
		
		// action
		service.deleteReminder(id, DEFAULT_USER);
		
		// verification
		assertEquals(count-1, ReminderEntity.count());
	}
	
	@Test
	void shouldRemoveOrphanEntitiesWhenDeleting() {
		// data
		long timeCount = TimeEntity.count();
	    long id = repository.listAll().stream().filter(r -> r.time != null).mapToLong(e -> e.id).max().getAsLong();
		
		// action
		service.deleteReminder(id, DEFAULT_USER);
		
		// verification
		assertEquals(timeCount-1, TimeEntity.count());
	}
}
