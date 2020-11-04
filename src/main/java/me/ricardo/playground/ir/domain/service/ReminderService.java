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

	public List<Reminder> getReminders() {
		return reminderRepository.listAll()
				                 .stream()
								 .map(ReminderAdapter::fromStorage)
								 .collect(Collectors.toList());
	}

	public Reminder getReminder(long id) {
		return reminderRepository.findByIdOptional(id)
				                 .map(ReminderAdapter::fromStorage)
				                 .orElseThrow(() -> new NotFoundException());
	}

	public Reminder updateReminder(long id, Reminder reminder) {
		ReminderEntity entity = reminderRepository.findByIdOptional(id)
												  .map(e -> ReminderAdapter.toStorage(reminder,
														                              new Metadata(e.createdAt, clock.instant().getEpochSecond()),
														                              e))
												  .orElseThrow(() -> new NotFoundException());
		
		reminderRepository.persist(entity);
		
		return ReminderAdapter.fromStorage(entity);
	}

	public void deleteReminder(long id) {
		if (!reminderRepository.deleteById(id)) {
			throw new NotFoundException();
		}
	}

	public List<Long> getSchedule(long id, long start, long end) {
		return getReminder(id).schedule(start)
				              .takeWhile(s -> s < end)
				              .collect(Collectors.toList());
	}
}
