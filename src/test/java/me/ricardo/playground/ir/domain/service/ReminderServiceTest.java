package me.ricardo.playground.ir.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Validation;
import javax.validation.executable.ExecutableValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.adapter.ReminderAdapter;
import me.ricardo.playground.ir.domain.doubles.ReminderFakes;
import me.ricardo.playground.ir.domain.doubles.ReminderRepositoryFake;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.Bound;
import me.ricardo.playground.ir.domain.entity.repetion.DailyRepetion;
import me.ricardo.playground.ir.domain.entity.repetion.Time;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

class ReminderServiceTest {

	@Nested
	class ScheduleReminder {
	    private final static long TIMESTAMP = 1000L;
	    
	    private ReminderService svc;
	    
	    private ReminderCrud crud;
	    
	    private ReminderRepository repository;
	    
	    private ExecutableValidator validator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();

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
			List<Long> schedule = svc.getSchedule(result.getId(), "user", 0, Bound.timestamp(200000L));

			// verification
			assertEquals(60L, schedule.get(0));
			assertEquals(86460L, schedule.get(1));
			assertEquals(172860L, schedule.get(2));
		}

		@Test
		void shouldRetrieveReminderScheduleWithLimit() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			Reminder result = crud.createReminder(reminder);

			// action
			List<Long> schedule = svc.getSchedule(result.getId(), "user", 0, Bound.count(1L));

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
			List<Long> schedule = svc.getSchedule(result.getId(), "notTheUser", 0, Bound.timestamp(200000L));

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
			List<Long> schedule = svc.getSchedule(999L, "user", 0, Bound.timestamp(200000L));

			// verification
			assertEquals(0, schedule.size());
		}

		@Test
		void shouldNotRetrieveSchedulerWithoutBound() throws NoSuchMethodException, SecurityException {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			crud.createReminder(reminder);

			// verification
			assertEquals(1, validator.validateParameters(svc, ReminderService.class.getMethod("getSchedule", long.class, String.class, long.class, Bound.class), new Object[] {999L, "user", 0, Bound.none()}).size());
		}

		@Test
		void shouldNotRetrieveSchedulerWithNullBound() throws NoSuchMethodException, SecurityException {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			crud.createReminder(reminder);

			// verification
            assertEquals(1, validator.validateParameters(svc, ReminderService.class.getMethod("getSchedule", long.class, String.class, long.class, Bound.class), new Object[] {999L, "user", 0, null}).size());
		}
		
		@Test
        void shouldNotRetrieveSchedulerWithNegativeCountBound() throws NoSuchMethodException, SecurityException {
            // data
            Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
            Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

            crud.createReminder(reminder);

            // verification
            assertEquals(1, validator.validateParameters(svc, ReminderService.class.getMethod("getSchedule", long.class, String.class, long.class, Bound.class), new Object[] {999L, "user", 0, Bound.count(-1)}).size());
        }
		
		@Test
        void shouldNotRetrieveSchedulerWithNegativeTimeBound() throws NoSuchMethodException, SecurityException {
            // data
            Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
            Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

            crud.createReminder(reminder);

            // verification
            assertEquals(1, validator.validateParameters(svc, ReminderService.class.getMethod("getSchedule", long.class, String.class, long.class, Bound.class), new Object[] {999L, "user", 0, Bound.timestamp(-1)}).size());
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
	
	@Nested
	class TruncateReminder {
	    @Test
	    void shouldBeAbleToTruncateReminder() {
	        // data
	        ReminderEntity reminder = ReminderFakes.DAILY_REPETION();
	        ReminderService svc = new ReminderService(new ReminderRepositoryFake(reminder), null);
	        long timestamp = 1036860;
	        assertEquals(List.of(1036860L), ReminderAdapter.fromStorage(reminder).getTime().schedule(timestamp).limit(1).collect(Collectors.toList()));
	        
	        // action
	        Reminder result = svc.truncate(1L, "user", timestamp).get();
	        
	        // verification
	        assertEquals(reminder.id, result.getId());
	        assertEquals(reminder.content, result.getContent());
	        assertEquals(reminder.createdAt, result.getMetadata().createdAt());
	        assertEquals(2, reminder.time.boundType);
	        assertEquals(List.of(), result.getTime().schedule(timestamp).collect(Collectors.toList()));
	    }
	    
	    @Test
        void shouldReturnNothingWhenDailyRepetionTruncatedBefore() {
            // data
            ReminderEntity reminder = ReminderFakes.DAILY_REPETION();
            ReminderRepository repository = new ReminderRepositoryFake(reminder);
            ReminderCrud crud = new ReminderCrud(repository, null);
            ReminderService svc = new ReminderService(repository, crud);
            
            // action
            Optional<Reminder> result = svc.truncate(reminder.id, "user", reminder.time.time);
            
            // verification
            assertEquals(Optional.empty(), result);
        }
	    
	    @Test
	    void shouldNotReturnInexistentReminder() {
	        // data
	        ReminderService svc = new ReminderService(new ReminderRepositoryFake(), null);
	        
	        // verification
	        assertEquals(Optional.empty(), svc.truncate(999L, "user", 0L));
	    }
	    
	    @Test
        void shouldNotReturnOtherUserReminder() {
            // data
            ReminderService svc = new ReminderService(new ReminderRepositoryFake(ReminderFakes.DAILY_REPETION()), null);
            
            // verification
            assertEquals(Optional.empty(), svc.truncate(1L, "notTheUser", 0L));
        }
	    
	    @Test
        void shouldReturnReminderWithoutTimeUnchanged() {
            // data
	        ReminderEntity reminder = ReminderFakes.SIMPLE_REMINDER();
            ReminderService svc = new ReminderService(new ReminderRepositoryFake(reminder), null);
            
            // verification
            assertEquals(Optional.of(ReminderAdapter.fromStorage(reminder)), svc.truncate(1L, "user", 0L));
        }
	    
	    @Test
	    void shouldReturnFixedTimeUnchangedWhenTruncatedAfter() {
	        // data
	        ReminderEntity reminder = ReminderFakes.FIXED_TIME();
	        ReminderService svc = new ReminderService(new ReminderRepositoryFake(reminder), null);
	        
	        // action
	        Optional<Reminder> result = svc.truncate(reminder.id, "user", reminder.time.time + 1);
	        
	        // verification
	        assertEquals(Optional.of(ReminderAdapter.fromStorage(reminder)), result);
	    }
	    
	    @Test
        void shouldReturnNothingWhenFixedTimeTruncatedBefore() {
            // data
            ReminderEntity reminder = ReminderFakes.FIXED_TIME();
            ReminderRepository repository = new ReminderRepositoryFake(reminder);
            ReminderCrud crud = new ReminderCrud(repository, null);
            ReminderService svc = new ReminderService(repository, crud);
            
            // action
            Optional<Reminder> result = svc.truncate(reminder.id, "user", reminder.time.time);
            
            // verification
            assertEquals(Optional.empty(), result);
        }
	}
}
