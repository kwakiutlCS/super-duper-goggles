package me.ricardo.playground.ir.domain.service;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.ws.rs.NotFoundException;

import me.ricardo.playground.ir.domain.adapter.ReminderAdapter;
import me.ricardo.playground.ir.domain.entity.Metadata;
import me.ricardo.playground.ir.domain.entity.Reminder;
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

	public Reminder createReminder(Reminder reminder) {
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

	public Reminder getReminder(long id, String user) {
		return reminderRepository.findByIdOptional(id)
								 .filter(r -> user.equals(r.userId))
				                 .map(ReminderAdapter::fromStorage)
				                 .orElseThrow(NotFoundException::new);
	}

	public Reminder updateReminder(long id, String user, Reminder reminder) {
		ReminderEntity entity = reminderRepository.findByIdOptional(id)
				                                  .filter(r -> user.equals(r.userId))
												  .map(e -> ReminderAdapter.toStorage(reminder,
														                              new Metadata(e.createdAt, clock.instant().getEpochSecond()),
														                              e))
												  .orElseThrow(NotFoundException::new);
		
		reminderRepository.persist(entity);
		
		return ReminderAdapter.fromStorage(entity);
	}

	public void deleteReminder(long id, String user) {
		if (reminderRepository.deleteUserReminderById(id, user) == 0) {
			throw new NotFoundException();
		}
	}

	public List<Long> getSchedule(long id, String user, long start, long end) {
		return getReminder(id, user).schedule(start)
				                    .takeWhile(s -> s < end)
				                    .collect(Collectors.toList());
	}
}
