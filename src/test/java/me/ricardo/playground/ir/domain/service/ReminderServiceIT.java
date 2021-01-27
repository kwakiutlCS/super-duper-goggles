package me.ricardo.playground.ir.domain.service;

import static me.ricardo.playground.ir.domain.doubles.ReminderFakes.DAILY_REPETION;
import static me.ricardo.playground.ir.domain.doubles.ReminderFakes.FIXED_TIME;
import static me.ricardo.playground.ir.domain.doubles.ReminderFakes.SIMPLE_REMINDER;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import me.ricardo.playground.ir.domain.adapter.ReminderAdapter;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.Bound;
import me.ricardo.playground.ir.domain.entity.repetion.FixedTime;
import me.ricardo.playground.ir.domain.entity.repetion.NoTime;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.entity.TimeEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

@QuarkusTest
@QuarkusTestResource(value = PostgresqlResource.class)
class ReminderServiceIT {

    private static List<ReminderEntity> REMINDERS = List.of(SIMPLE_REMINDER(), DAILY_REPETION(), DAILY_REPETION(),  DAILY_REPETION(), DAILY_REPETION(), FIXED_TIME(), DAILY_REPETION());
    
	@Inject
	ReminderCrud crud;
	
	@Inject
	ReminderService service;
	
	@Inject
	ReminderRepository repository;
	
	@Inject
	TransactionManager tm;
	
	@BeforeAll
	@Transactional
	static void init() {
	    for (ReminderEntity reminder : REMINDERS) {
	        reminder.persist();
	    }
	}
	
	@Test
	void shouldCreateReminder() {
		// data
		long count = repository.count();
		Reminder reminder = Reminder.Builder.start().withUser("user").build();
		
		// action
		Reminder result = crud.createReminder(reminder);
		
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
		List<Reminder> userReminders = crud.getReminders("user");
		List<Reminder> otherUserReminders = crud.getReminders("otherUser");
		
		// verification
		assertEquals(count, userReminders.size());
		assertEquals(0, otherUserReminders.size());
	}
	
	@Test
	void shouldFindReminderById() {
		// action
		Optional<Reminder> userReminder = crud.getReminder(1L, "user");
		Optional<Reminder> otherUserReminder = crud.getReminder(1L, "otherUser");
		
		// verification
		assertEquals(1L, userReminder.get().getId());
		assertEquals(Optional.empty(), otherUserReminder);
	}
	
	@Test
	void shouldUpdateReminder() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		// data
		Reminder reminder = Reminder.Builder.start().withContent("updated").withId(1L).withUser("user").build();
		
		//action
		crud.updateReminder(reminder);
		
		// verification
		tm.begin();
		assertEquals("updated", repository.findById(1L).content);
		tm.commit();
	}
	
	@Test
    void shouldRemoveOrphanEntitiesWhenUpdating() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        // data
	    long timeCount = TimeEntity.count();
        long id = REMINDERS.get(1).id;
        Reminder reminder = Reminder.Builder.start().withContent("updated").withId(id).withUser("user").build();
        
        //action
        Reminder result = crud.updateReminder(reminder).get();
        
        // verification
        tm.begin();
        assertEquals(NoTime.INSTANCE, result.getTime());
        assertEquals(timeCount-1, TimeEntity.count());
        tm.commit();
    }
	
	@Test
    void shouldUpdateChildEntity() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        // data
        long id = REMINDERS.get(5).id; 
        long childId = repository.findById(id).time.id;
        Reminder reminder = Reminder.Builder.start().withContent("updated").withId(id).withTime(new FixedTime(4L)).withUser("user").build();
        
        //action
        Reminder result = crud.updateReminder(reminder).get();
        
        // verification
        tm.begin();
        assertNotEquals(NoTime.INSTANCE, result.getTime());
        assertEquals(childId, repository.findById(id).time.id);
        tm.commit();
    }
	
	@Test
	void shouldDeleteReminder() {
		// data
		long id = REMINDERS.get(5).id;
		long count = ReminderEntity.count();
		
		// action
		crud.deleteReminder(id, "user");
		
		// verification
		assertEquals(count-1, ReminderEntity.count());
	}
	
	@Test
	void shouldRemoveOrphanEntitiesWhenDeleting() {
        // data
		long timeCount = TimeEntity.count();
	    long id = REMINDERS.get(3).id;		
		
	    // action
		crud.deleteReminder(id, "user");
		
		// verification
		assertEquals(timeCount-1, TimeEntity.count());
	}
	
	@Test
	void shouldAddException() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
	    // data
        long id = REMINDERS.get(2).id;
        assertEquals(Set.of(), repository.findById(id).time.exceptions);
        
        // action
        boolean result = service.addException(id, "user", 60L);
        
        // verification
        tm.begin();
        assertTrue(result);
        assertEquals(Set.of(60L), repository.findById(id).time.exceptions);
        tm.commit();
	}
	
	@Test
	void shouldTruncateReminder() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
	    // data
	    long id = REMINDERS.get(6).id;
	    long timestamp = REMINDERS.get(6).time.time;
	    assertEquals(List.of(timestamp, timestamp + 86400), service.getSchedule(id, "user", timestamp, Bound.count(2)));
	    
	    // action
	    Optional<Reminder> result = service.truncate(id, "user", timestamp + 1);
	    
	    // verification
	    tm.begin();
	    assertEquals(List.of(timestamp), ReminderAdapter.fromStorage(repository.findById(id)).getTime().schedule().limit(2).collect(Collectors.toList()));
	    assertEquals(List.of(timestamp), result.get().getTime().schedule().limit(2).collect(Collectors.toList()));
	    tm.commit();
	}
	
	@Test
    void shouldDeleteTruncateReminderIfNoSchedulingRemains() throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        // data
        long id = REMINDERS.get(6).id;
        long timestamp = REMINDERS.get(6).time.time;
        assertEquals(List.of(timestamp), service.getSchedule(id, "user", timestamp, Bound.count(1)));
        
        // action
        Optional<Reminder> result = service.truncate(id, "user", timestamp);
        
        // verification
        tm.begin();
        assertNull(repository.findById(id));
        assertEquals(Optional.empty(), result);
        tm.commit();
    }
}
