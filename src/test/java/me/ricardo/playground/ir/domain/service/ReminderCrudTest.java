package me.ricardo.playground.ir.domain.service;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import javax.validation.Validation;
import javax.validation.executable.ExecutableValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.doubles.ReminderFakes;
import me.ricardo.playground.ir.domain.doubles.ReminderRepositoryFake;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.Bound;
import me.ricardo.playground.ir.domain.entity.repetion.DailyRepetion;
import me.ricardo.playground.ir.domain.entity.repetion.FixedTime;
import me.ricardo.playground.ir.domain.entity.repetion.Time;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

class ReminderCrudTest {

	private final static long TIMESTAMP = 1000L;
	
	private ReminderCrud crud;
	
	private ReminderRepository repository;
	
	private static ExecutableValidator validator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();

	@BeforeEach
	void init() {
	    repository = new ReminderRepositoryFake(ReminderFakes.SIMPLE_REMINDER(), ReminderFakes.DAILY_REPETION(), ReminderFakes.FIXED_TIME());
	    crud = new ReminderCrud(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP), ZoneOffset.UTC));
	}

	@Nested
	class FindReminders {
		@Test
		void shouldFindAllReminders() {
			assertEquals(3, crud.getReminders("user").size());
		}

		@Test
		void shouldNotFindRemindersForDifferentUser() {
            assertEquals(0, crud.getReminders("notTheUser").size());
		}
	}
	
	@Nested
	class FindReminderById {
		@Test
		void shouldFindReminderById() {
			assertEquals("1", crud.getReminder(1L, "user").get().getContent());
		}

		@Test
		void shouldFindReminderWithoutUser() {
			assertTrue(crud.getReminder(1L, null).isEmpty());
		}

		@Test
		void shouldNotFindReminderByIdForDifferentUser() {
			assertTrue(crud.getReminder(1L, "notTheUser").isEmpty());
		}

		@Test
		void shouldNotFindNonExistingReminder() {
			assertTrue(crud.getReminder(999L, "user").isEmpty());
		}
	}

	@Nested
	class CreateReminders {
		@Test
		void shouldAddCreateTimeAndUpdateTimeToReminderCreated() {
			// data
			Reminder reminder = Reminder.Builder.start().withUser("user").build();

			// action
			Reminder result = crud.createReminder(reminder);

			// verification
			assertEquals(4L, result.getId());
			assertEquals(TIMESTAMP, result.getMetadata().createdAt());
			assertEquals(TIMESTAMP, result.getMetadata().updatedAt());
		}
		
		@Test
		void shouldCreateFixedTimeReminder() {
			// data
			Time time = new FixedTime(60L);
			Reminder reminder = Reminder.Builder.start().withUser("user").withTime(time).build();
			
			// action
			Reminder result = crud.createReminder(reminder);
			
			// verification
			assertEquals(60L, ((FixedTime)result.getTime()).getTime());
		}
		
		@Test
		void shouldCreateDailyRepetionTimeReminderBoundCount() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.count(3L), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withUser("user").withTime(time).build();
			
			// action
			Reminder result = crud.createReminder(reminder);
			
			// verification
			assertEquals(1, ((DailyRepetion) result.getTime()).getStep());
			assertEquals(ZoneOffset.UTC, ((DailyRepetion) result.getTime()).getZone());
			assertEquals(3, ((DailyRepetion) result.getTime()).getBound().limit());
			assertEquals(0, ((DailyRepetion) result.getTime()).getExceptions().size());
		}
		
		@Test
		void shouldCreateDailyRepetionTimeReminderBoundTimestamp() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.timestamp(90L), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withUser("user").withTime(time).build();
			
			// action
			Reminder result = crud.createReminder(reminder);
			
			// verification
			assertEquals(1, ((DailyRepetion) result.getTime()).getStep());
			assertEquals(ZoneOffset.UTC, ((DailyRepetion) result.getTime()).getZone());
			assertEquals(90L, ((DailyRepetion) result.getTime()).getBound().timestamp());
			assertEquals(0, ((DailyRepetion) result.getTime()).getExceptions().size());
		}
		
		@Test
		void shouldNotAllowIdInReminder() throws NoSuchMethodException, SecurityException {
            // data
			Reminder reminder = Reminder.Builder.start().withId(1L).withUser("user").build();
			
			// verification
			assertFalse(validator.validateParameters(crud, ReminderCrud.class.getDeclaredMethod("createReminder", Reminder.class), new Object[]{reminder}).isEmpty());
		}
		
		@Test
		void shouldNotAllowInvalidFixedTimeRemider() throws NoSuchMethodException, SecurityException {
		    // data
		    Reminder reminder = Reminder.Builder.start().withUser("user").withTime(new FixedTime(-80)).build();
		    
		    // verification
		    assertFalse(validator.validateParameters(crud, ReminderCrud.class.getDeclaredMethod("createReminder", Reminder.class), new Object[]{reminder}).isEmpty());
		}
		
		@Test
        void shouldNotAllowInvalidStepInDailyRepetionRemider() throws NoSuchMethodException, SecurityException {
            // data
            Reminder reminder = Reminder.Builder.start().withUser("user").withTime(new DailyRepetion(0, 0, Bound.none(), ZoneOffset.UTC)).build();
            
            // verification
            assertFalse(validator.validateParameters(crud, ReminderCrud.class.getDeclaredMethod("createReminder", Reminder.class), new Object[]{reminder}).isEmpty());
        }
	}
	
	@Nested
	class UpdateReminders {
		@Test
		void shouldUpdateReminderContent() {
			// data
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").build();
			long id = crud.createReminder(reminder).getId();

			// action
			ReminderCrud svc2 = new ReminderCrud(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP + 1), ZoneOffset.UTC));
			Optional<Reminder> result = svc2.updateReminder(Reminder.Builder.start().withUser("user").withId(id).withContent("updated").build());

			// verification
			assertEquals(TIMESTAMP, result.get().getMetadata().createdAt());
			assertEquals(TIMESTAMP + 1, result.get().getMetadata().updatedAt());
			assertEquals("updated", result.get().getContent());
			assertEquals(id, result.get().getId());
		}

		@Test
		void shouldNotUpdateReminderForOtherUser() {
			// data
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").build();
			long id = crud.createReminder(reminder).getId();

			// action
			ReminderCrud svc2 = new ReminderCrud(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP + 1), ZoneOffset.UTC));
			Optional<Reminder> result = svc2.updateReminder(Reminder.Builder.start().withUser("notTheUser").withId(id).withContent("updated").build());

			// verification
			assertTrue(result.isEmpty());
		}

		@Test
		void shouldNotUpdateInexistentReminder() {
			// data
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").build();
			crud.createReminder(reminder);

			// action
			ReminderCrud svc2 = new ReminderCrud(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP + 1), ZoneOffset.UTC));
			Optional<Reminder> result = svc2.updateReminder(Reminder.Builder.start().withUser("user").withId(999L).withContent("updated").build());

			// verification
			assertTrue(result.isEmpty());
		}
		
		@Test
		void shouldRequireIdWhenUpdatingReminder() throws NoSuchMethodException, SecurityException {
			// data
			Reminder reminder = Reminder.Builder.start().withUser("user").build();
						
			// verification
			assertFalse(validator.validateParameters(crud, ReminderCrud.class.getDeclaredMethod("updateReminder", Reminder.class), new Object[]{reminder}).isEmpty());
		}
		
		@Test
		void shouldAllowRemoveTimeInformation() {
		    // data
            Reminder reminder = Reminder.Builder.start().withId(3L).withUser("user").build();
            
            // action
            Optional<Reminder> result = crud.updateReminder(reminder);
            
            // verification
            assertNull(result.get().getTime());
		}
		
		@Test
        void shouldAllowAddingTimeInformation() {
            // data
            Reminder reminder = Reminder.Builder.start().withId(1L).withUser("user").withTime(new FixedTime(3L)).build();
            
            // action
            Optional<Reminder> result = crud.updateReminder(reminder);
            
            // verification
            assertNotNull(result.get().getTime());
        }
	}
	
	
	@Nested
	class DeleteReminder {
		@Test
		void shouldDeleteReminder() {
			// data
			Reminder reminder = Reminder.Builder.start().withContent("original").withUser("user").build();
			long id = crud.createReminder(reminder).getId();

			// action
			boolean result = crud.deleteReminder(id, "user");

			// verification
			assertEquals(true, result);
			assertTrue(crud.getReminder(id, "user").isEmpty());
		}

		@Test
		void shouldNotDeleteReminderForOtherUser() {
			// data
			Reminder reminder = Reminder.Builder.start().withContent("original").withUser("user").build();
			long id = crud.createReminder(reminder).getId();

			// action
			boolean result = crud.deleteReminder(id, "notTheUser");

			// verification
			assertEquals(false, result);
		}

		@Test
		void shouldNotDeleteInexistentReminder() {
			// data
			Reminder reminder = Reminder.Builder.start().withContent("original").withUser("user").build();
			crud.createReminder(reminder);

			// action
			boolean result = crud.deleteReminder(999L, "user");

			// verification
			assertEquals(false, result);
		}
		
		@Test
		void shouldReturnResultFromDeletionOperation() {
			// data
			ReminderCrud crud = new ReminderCrud(ReminderRepositoryFake.getNoDelete(), Clock.fixed(Instant.ofEpochSecond(TIMESTAMP), ZoneOffset.UTC));
			Reminder reminder = Reminder.Builder.start().withUser("user").build();
			crud.createReminder(reminder);
			
			// action
			boolean result = crud.deleteReminder(1L, "user");
			
			// verification
			assertEquals(false, result);
		}
	}
}
