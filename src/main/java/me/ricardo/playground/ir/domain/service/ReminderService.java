package me.ricardo.playground.ir.domain.service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;

import me.ricardo.playground.ir.domain.adapter.ReminderAdapter;
import me.ricardo.playground.ir.domain.entity.Metadata;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.validation.ReminderUpdate;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

@Dependent
public class ReminderService {

	private ReminderRepository reminderRepository;
	
	private Clock clock;
	
	public ReminderService(ReminderRepository reminderRepository, Clock clock) {
		this.reminderRepository = reminderRepository;
		this.clock = clock;
	}

	public Reminder createReminder(@Valid Reminder reminder) {
		ReminderEntity entity = ReminderAdapter.toStorage(reminder, new Metadata(clock.instant().getEpochSecond()));
		reminderRepository.persist(entity);

		return ReminderAdapter.fromStorage(entity);
	}

	public List<Reminder> getReminders(String user) {
		return reminderRepository.findByUser(user)
				                 .stream()
				                 .map(ReminderAdapter::fromStorage)
								 .collect(Collectors.toList());
	}

	public Optional<Reminder> getReminder(long id, String user) {
		return reminderRepository.findByIdOptional(id)
								 .filter(r -> r.userId.equals(user))
				                 .map(ReminderAdapter::fromStorage);
	}

	public Optional<Reminder> updateReminder(@Valid @ConvertGroup(from=Default.class, to=ReminderUpdate.class) Reminder reminder) {
		Optional<ReminderEntity> entity = reminderRepository.findByIdOptional(reminder.getId())
				                                  .filter(r -> r.userId.equals(reminder.getUser()))
												  .map(e -> ReminderAdapter.toStorage(reminder,
														                              new Metadata(e.createdAt, clock.instant().getEpochSecond()),
														                              e));
		
		entity.ifPresent(reminderRepository::persist);
		
		return entity.map(ReminderAdapter::fromStorage);
	}

	public boolean deleteReminder(long id, String user) {
		Optional<ReminderEntity> reminder = reminderRepository.findByIdOptional(id)
		                                                      .filter(r -> r.userId.equals(user));
		
		reminder.ifPresent(r -> reminderRepository.deleteById(r.id));
		
		return reminder.isPresent();
	}

	public List<Long> getSchedule(long id, String user, List<Long> interval, Long limit) {
		if (interval == null || interval.isEmpty()) {
			return List.of();
		}
		
		Stream<Long> schedule = getReminder(id, user).map(r -> r.schedule(interval.get(0)))
				                                     .orElse(Stream.empty());
		
		if (interval.size() == 2) {
			schedule = schedule.takeWhile(s -> s < interval.get(1));
		}
		
		if (limit != null) {
			schedule = schedule.limit(limit);
		}
		
		return schedule.collect(Collectors.toList());
	}
}
