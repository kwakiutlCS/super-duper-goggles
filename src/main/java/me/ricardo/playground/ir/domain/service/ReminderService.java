package me.ricardo.playground.ir.domain.service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import me.ricardo.playground.ir.domain.adapter.ReminderAdapter;
import me.ricardo.playground.ir.domain.entities.Reminder;
import me.ricardo.playground.ir.storage.entities.ReminderEntity;
import me.ricardo.playground.ir.storage.repositories.ReminderRepository;

@Dependent
public class ReminderService {

	private ReminderRepository reminderRepository;
	
	private Clock clock;
	
	public ReminderService(ReminderRepository reminderRepository, Clock clock) {
		this.reminderRepository = reminderRepository;
		this.clock = clock;
	}
	
	public Reminder createReminder(Reminder reminder) {
		long timestamp = clock.instant().getEpochSecond();
		reminder.setCreatedAt(timestamp);
		reminder.setUpdatedAt(timestamp);

		ReminderEntity entity = ReminderAdapter.toStorage(reminder);
		reminderRepository.persist(entity);

		return ReminderAdapter.fromStorage(entity);
	}

	public List<Reminder> getReminders() {
		return reminderRepository.listAll()
				                 .stream()
								 .map(ReminderAdapter::fromStorage)
								 .collect(Collectors.toList());
	}

	public Optional<Reminder> getReminder(long id) {
		return reminderRepository.findByIdOptional(id)
				                 .map(ReminderAdapter::fromStorage);
	}
}
