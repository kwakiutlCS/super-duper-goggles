package me.ricardo.playground.ir.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.doubles.ReminderRepositoryFake;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.Bound;
import me.ricardo.playground.ir.domain.entity.repetion.DailyRepetion;
import me.ricardo.playground.ir.domain.entity.repetion.Time;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.entity.TimeEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

class ReminderServiceTest {

	private final static long TIMESTAMP = 1000L;
	
	private ReminderService service;
	
	private ReminderCrud crud;
	
	private ReminderRepository repository;
	
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
		
		TimeEntity timeEntity = new TimeEntity();
		timeEntity.time = 60L;
		ReminderEntity timeReminder = new ReminderEntity();
		timeReminder.content = "3";
		timeReminder.userId = "user";
		timeReminder.createdAt = 0;
		timeReminder.updatedAt = 0;
		timeReminder.time = timeEntity;
		
		TimeEntity timeEntity1 = new TimeEntity();
		timeEntity1.time = 60L;
		timeEntity1.step = 1;
		timeEntity1.unit = ChronoUnit.DAYS;
		timeEntity1.boundType = 0;
		timeEntity1.boundValue = 1L;
		timeEntity1.zone = "Z";
		ReminderEntity timeReminder1 = new ReminderEntity();
		timeReminder1.content = "3";
		timeReminder1.userId = "user";
		timeReminder1.createdAt = 0;
		timeReminder1.updatedAt = 0;
		timeReminder1.time = timeEntity1;
		
		repository = new ReminderRepositoryFake();
		repository.persist(entity1);
		repository.persist(entity2);
		repository.persist(timeReminder);
		repository.persist(timeReminder1);
		
		crud = new ReminderCrud(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP), ZoneOffset.UTC));
		service = new ReminderService(repository, crud);
	}

	
	@Nested
	class ScheduleReminder {
		@Test
		void shouldRetrieveReminderSchedule() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			Reminder result = crud.createReminder(reminder);

			// action
			List<Long> schedule = service.getSchedule(result.getId(), "user", List.of(0L, 200000L), null);

			// verification
			assertEquals(60L, schedule.get(0));
			assertEquals(86460L, schedule.get(1));
			assertEquals(172860L, schedule.get(2));
		}

		@Test
		void shouldRetrieveReminderScheduleWithEndAndLimit() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			Reminder result = crud.createReminder(reminder);

			// action
			List<Long> schedule = service.getSchedule(result.getId(), "user", List.of(0L, 200000L), 1L);

			// verification
			assertEquals(60L, schedule.get(0));
			assertEquals(1, schedule.size());
		}

		@Test
		void shouldRetrieveReminderScheduleWithEndAndLimit2() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			Reminder result = crud.createReminder(reminder);

			// action
			List<Long> schedule = service.getSchedule(result.getId(), "user", List.of(120L, 121L), 1L);

			// verification
			assertEquals(0, schedule.size());
		}

		@Test
		void shouldRetrieveReminderScheduleWithLimit() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			Reminder result = crud.createReminder(reminder);

			// action
			List<Long> schedule = service.getSchedule(result.getId(), "user", List.of(0L), 1L);

			// verification
			assertEquals(60L, schedule.get(0));
			assertEquals(1, schedule.size());
		}

		@Test
		void shouldNotRetrieveReminderScheduleFromDifferentUser() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			Reminder result = crud.createReminder(reminder);

			// action
			List<Long> schedule = service.getSchedule(result.getId(), "notTheUser", List.of(0L, 200000L), null);

			// verification
			assertEquals(0, schedule.size());
		}

		@Test
		void shouldNotRetrieveNonExistentReminderSchedule() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			crud.createReminder(reminder);

			// action
			List<Long> schedule = service.getSchedule(999L, "user", List.of(0L, 200000L), null);

			// verification
			assertEquals(0, schedule.size());
		}

		@Test
		void shouldNotRetrieveSchedulerWithoutInterval() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			crud.createReminder(reminder);

			// action
			List<Long> schedule = service.getSchedule(999L, "user", List.of(), null);

			// verification
			assertEquals(0, schedule.size());
		}

		@Test
		void shouldNotRetrieveSchedulerWithNullInterval() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			crud.createReminder(reminder);

			// action
			List<Long> schedule = service.getSchedule(999L, "user", null, null);

			// verification
			assertEquals(0, schedule.size());
		}
	}
	
	@Nested
	class AddException {
	    @Test
	    void shouldAddException() {
	        // data
	        long id = repository.findByUser("user").stream().filter(r -> r.time != null).mapToLong(r -> r.id).max().getAsLong();

	        // action
	        boolean result = service.addException(id, "user", 60L);

	        // verification
	        assertTrue(result);
	    }

	    @Test
	    void shouldNotAddInexistentException() {
	        // data
	        long id = repository.findByUser("user").stream().filter(r -> r.time != null).mapToLong(r -> r.id).max().getAsLong();

            // action
	        boolean result = service.addException(id, "user", 0L);

	        // verification
	        assertFalse(result);
	    }

	    @Test
	    void shouldNotAddExceptionForOtherUser() {
	        // data
	        long id = repository.findByUser("user").stream().filter(r -> r.time != null).mapToLong(r -> r.id).max().getAsLong();

	        // action
	        boolean result = service.addException(id, "otherUser", 60L);

	        // verification
	        assertFalse(result);
	    }
	    
	    @Test
	    void shouldNotAllowAddingExceptionForFixedTimeReminder() {
	        // data
            long id = repository.findByUser("user").stream().filter(r -> r.time != null).mapToLong(r -> r.id).min().getAsLong();

            // action
            boolean result = service.addException(id, "user", 60L);
            
            // verification
            assertFalse(result);
	    }
	    
	    @Test
        void shouldNotAllowAddingExceptionForNonTimeReminder() {
            // data
            long id = repository.findByUser("user").stream().filter(r -> r.time == null).mapToLong(r -> r.id).min().getAsLong();

            // action
            boolean result = service.addException(id, "user", 60L);
            
            // verification
            assertFalse(result);
        }
	    
	    @Test
        void shouldNotAddExceptionForInexistentReminder() {
	        // action
            boolean result = service.addException(999L, "user", 0L);

            // verification
            assertFalse(result);
        }
	}
}
