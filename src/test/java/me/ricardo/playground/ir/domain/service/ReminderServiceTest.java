package me.ricardo.playground.ir.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.Bound;
import me.ricardo.playground.ir.domain.entity.repetion.DailyRepetion;
import me.ricardo.playground.ir.domain.entity.repetion.FixedTime;
import me.ricardo.playground.ir.domain.entity.repetion.Time;
import me.ricardo.playground.ir.domain.mocks.ReminderRepositoryFake;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

class ReminderServiceTest {

	private final static long TIMESTAMP = 1000L;
	
	private ReminderService service;
	
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
		
		repository = new ReminderRepositoryFake();
		repository.persist(entity1);
		repository.persist(entity2);
		
		service = new ReminderService(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP), ZoneOffset.UTC));
	}

	@Nested
	class FindReminders {
		@Test
		void shouldFindAllReminders() {
			assertEquals(2, service.getReminders("user").size());
		}

		@Test
		void shouldNotFindRemindersForDifferentUser() {
			assertEquals(0, service.getReminders("notTheUser").size());
		}
	}
	
	@Nested
	class FindReminderById {
		@Test
		void shouldFindReminderById() {
			assertEquals("1", service.getReminder(1L, "user").get().getContent());
		}

		@Test
		void shouldFindReminderWithoutUser() {
			assertTrue(service.getReminder(1L, null).isEmpty());
		}

		@Test
		void shouldNotFindReminderByIdForDifferentUser() {
			assertTrue(service.getReminder(1L, "notTheUser").isEmpty());
		}

		@Test
		void shouldNotFindNonExistingReminder() {
			assertTrue(service.getReminder(999L, "user").isEmpty());
		}
	}

	@Nested
	class CreateReminders {
		@Test
		void shouldAddCreateTimeAndUpdateTimeToReminderCreated() {
			// data
			Reminder reminder = Reminder.Builder.start().withUser("user").build();

			// action
			Reminder result = service.createReminder(reminder);

			// verification
			assertEquals(3L, result.getId());
			assertEquals(TIMESTAMP, result.getMetadata().getCreatedAt());
			assertEquals(TIMESTAMP, result.getMetadata().getUpdatedAt());
		}
		
		@Test
		void shouldCreateFixedTimeReminder() {
			// data
			Time time = new FixedTime(60L);
			Reminder reminder = Reminder.Builder.start().withUser("user").withTime(time).build();
			
			// action
			Reminder result = service.createReminder(reminder);
			
			// verification
			assertEquals(60L, ((FixedTime)result.getTime()).getTime());
		}
		
		@Test
		void shouldCreateDailyRepetionTimeReminder() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.count(3L), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withUser("user").withTime(time).build();
			
			// action
			Reminder result = service.createReminder(reminder);
			
			// verification
			assertEquals(1, ((DailyRepetion) result.getTime()).getStep());
			assertEquals(ZoneOffset.UTC, ((DailyRepetion) result.getTime()).getZone());
			assertEquals(3, ((DailyRepetion) result.getTime()).getBound().getLimit());
			assertEquals(0, ((DailyRepetion) result.getTime()).getExceptions().size());
		}
	}
	
	@Nested
	class UpdateReminders {
		@Test
		void shouldUpdateReminderContent() {
			// data
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").build();
			long id = service.createReminder(reminder).getId();

			// action
			ReminderService svc2 = new ReminderService(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP + 1), ZoneOffset.UTC));
			Optional<Reminder> result = svc2.updateReminder(id, "user", Reminder.Builder.start().withContent("updated").build());

			// verification
			assertEquals(TIMESTAMP, result.get().getMetadata().getCreatedAt());
			assertEquals(TIMESTAMP + 1, result.get().getMetadata().getUpdatedAt());
			assertEquals("updated", result.get().getContent());
			assertEquals(id, result.get().getId());
		}

		@Test
		void shouldNotUpdateReminderForOtherUser() {
			// data
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").build();
			long id = service.createReminder(reminder).getId();

			// action
			ReminderService svc2 = new ReminderService(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP + 1), ZoneOffset.UTC));
			Optional<Reminder> result = svc2.updateReminder(id, "notTheUser", Reminder.Builder.start().withContent("updated").build());

			// verification
			assertTrue(result.isEmpty());
		}

		@Test
		void shouldNotUpdateInexistentReminder() {
			// data
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").build();
			service.createReminder(reminder);

			// action
			ReminderService svc2 = new ReminderService(repository, Clock.fixed(Instant.ofEpochSecond(TIMESTAMP + 1), ZoneOffset.UTC));
			Optional<Reminder> result = svc2.updateReminder(999L, "user", Reminder.Builder.start().withContent("updated").build());

			// verification
			assertTrue(result.isEmpty());
		}
	}
	
	
	@Nested
	class DeleteReminder {
		@Test
		void shouldDeleteReminder() {
			// data
			Reminder reminder = Reminder.Builder.start().withContent("original").withUser("user").build();
			long id = service.createReminder(reminder).getId();

			// action
			long result = service.deleteReminder(id, "user");

			// verification
			assertEquals(1L, result);
			assertTrue(service.getReminder(id, "user").isEmpty());
		}

		@Test
		void shouldNotDeleteReminderForOtherUser() {
			// data
			Reminder reminder = Reminder.Builder.start().withContent("original").withUser("user").build();
			long id = service.createReminder(reminder).getId();

			// action
			long result = service.deleteReminder(id, "notTheUser");

			// verification
			assertEquals(0L, result);
		}

		@Test
		void shouldNotDeleteInexistentReminder() {
			// data
			Reminder reminder = Reminder.Builder.start().withContent("original").withUser("user").build();
			service.createReminder(reminder);

			// action
			long result = service.deleteReminder(999L, "user");

			// verification
			assertEquals(0L, result);
		}
	}

	
	@Nested
	class ScheduleReminder {
		@Test
		void shouldRetrieveReminderSchedule() {
			// data
			Time time = new DailyRepetion(60L, 1, Bound.none(), ZoneOffset.UTC);
			Reminder reminder = Reminder.Builder.start().withContent("content").withUser("user").withTime(time).build();

			Reminder result = service.createReminder(reminder);

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

			Reminder result = service.createReminder(reminder);

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

			Reminder result = service.createReminder(reminder);

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

			Reminder result = service.createReminder(reminder);

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

			Reminder result = service.createReminder(reminder);

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

			service.createReminder(reminder);

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

			service.createReminder(reminder);

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

			service.createReminder(reminder);

			// action
			List<Long> schedule = service.getSchedule(999L, "user", null, null);

			// verification
			assertEquals(0, schedule.size());
		}
	}
}
