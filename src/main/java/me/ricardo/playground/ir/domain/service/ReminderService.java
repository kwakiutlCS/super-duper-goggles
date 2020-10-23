package me.ricardo.playground.ir.domain.service;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.ws.rs.NotFoundException;

import me.ricardo.playground.ir.domain.adapter.ReminderAdapter;
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

	public Reminder getReminder(long id) {
		return reminderRepository.findByIdOptional(id)
				                 .map(ReminderAdapter::fromStorage)
				                 .orElseThrow(() -> new NotFoundException());
	}

	public Reminder updateReminder(long id, Reminder reminder) {
		reminder.setUpdatedAt(clock.instant().getEpochSecond());
		
		ReminderEntity entity = reminderRepository.findByIdOptional(id)
												  .map(e -> ReminderAdapter.toStorage(reminder, e))
												  .orElseThrow(() -> new NotFoundException());
		
		reminderRepository.persist(entity);
		
		return ReminderAdapter.fromStorage(entity);
	}

	public void deleteReminder(long id) {
		if (!reminderRepository.deleteById(id)) {
			throw new NotFoundException();
		}
	}
}
