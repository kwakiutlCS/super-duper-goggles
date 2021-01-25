package me.ricardo.playground.ir.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.doubles.ReminderFakes;
import me.ricardo.playground.ir.domain.doubles.ReminderRepositoryFake;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.Bound;
import me.ricardo.playground.ir.domain.entity.repetion.DailyRepetion;
import me.ricardo.playground.ir.domain.entity.repetion.Time;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

class ReminderServiceTest {

	@Nested
	class ScheduleReminder {
	    private final static long TIMESTAMP = 1000L;
	    
	    private ReminderService svc;
	    
	    private ReminderCrud crud;
	    
	    private ReminderRepository repository;

	    @BeforeEach
	    void init() {
	        repository = new ReminderRepositoryFake();
	        
	        crud = new ReminderCrud(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP), ZoneOffset.UTC));
	        svc = new ReminderService(null, crud);
	    }
	    
	    
		@Test
		void shouldRetrieveReminderSchedule() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			Reminder result = crud.createReminder(reminder);

			// action
			List<Long> schedule = svc.getSchedule(result.getId(), "user", List.of(0L, 200000L), null);

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
			List<Long> schedule = svc.getSchedule(result.getId(), "user", List.of(0L, 200000L), 1L);

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
			List<Long> schedule = svc.getSchedule(result.getId(), "user", List.of(120L, 121L), 1L);

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
			List<Long> schedule = svc.getSchedule(result.getId(), "user", List.of(0L), 1L);

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
			List<Long> schedule = svc.getSchedule(result.getId(), "notTheUser", List.of(0L, 200000L), null);

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
			List<Long> schedule = svc.getSchedule(999L, "user", List.of(0L, 200000L), null);

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
			List<Long> schedule = svc.getSchedule(999L, "user", List.of(), null);

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
			List<Long> schedule = svc.getSchedule(999L, "user", null, null);

			// verification
			assertEquals(0, schedule.size());
		}
	}
	
	@Nested
	class AddException {
	    @Test
	    void shouldAddException() {
	        // data
	        ReminderService svc = new ReminderService(new ReminderRepositoryFake(ReminderFakes.DAILY_REPETION()), null);

	        // action
	        boolean result = svc.addException(1L, "user", 60L);

	        // verification
	        assertTrue(result);
	    }

	    @Test
	    void shouldNotAddInexistentException() {
	        // data
            ReminderService svc = new ReminderService(new ReminderRepositoryFake(ReminderFakes.DAILY_REPETION()), null);

            // action
            boolean result = svc.addException(1L, "user", 0L);
            
	        // verification
	        assertFalse(result);
	    }

	    @Test
	    void shouldNotAddExceptionForOtherUser() {
	        // data
            ReminderService svc = new ReminderService(new ReminderRepositoryFake(ReminderFakes.DAILY_REPETION()), null);

            // action
            boolean result = svc.addException(1L, "notTheUser", 60L);

	        // verification
	        assertFalse(result);
	    }
	    
	    @Test
	    void shouldNotAllowAddingExceptionForFixedTimeReminder() {
	        // data
            ReminderService svc = new ReminderService(new ReminderRepositoryFake(ReminderFakes.FIXED_TIME()), null);

            // action
            boolean result = svc.addException(1L, "user", 60L);
            
            // verification
            assertFalse(result);
	    }
	    
	    @Test
        void shouldNotAllowAddingExceptionForNonTimeReminder() {
	        // data
            ReminderService svc = new ReminderService(new ReminderRepositoryFake(ReminderFakes.SIMPLE_REMINDER()), null);

            // action
            boolean result = svc.addException(1L, "user", 60L);
            
            // verification
            assertFalse(result);
        }
	    
	    @Test
        void shouldNotAddExceptionForInexistentReminder() {
	        // data
            ReminderService svc = new ReminderService(new ReminderRepositoryFake(), null);

            // action
            boolean result = svc.addException(1L, "user", 60L);

            // verification
            assertFalse(result);
        }
	}
}
