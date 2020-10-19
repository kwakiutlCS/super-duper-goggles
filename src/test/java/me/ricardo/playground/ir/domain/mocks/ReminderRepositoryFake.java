package me.ricardo.playground.ir.domain.mocks;

import java.util.HashMap;
import java.util.List;

import me.ricardo.playground.ir.storage.entities.ReminderEntity;
import me.ricardo.playground.ir.storage.repositories.ReminderRepository;

public class ReminderRepositoryFake extends ReminderRepository {

	private HashMap<Long, ReminderEntity> reminders = new HashMap<>();
	
	private static long next_id = 1L;
	
	public List<ReminderEntity> listAll() {
		return (List<ReminderEntity>) reminders.values();
	}
	
	public void persist(ReminderEntity entity) {
		entity.id = next_id;
		reminders.put(next_id++, entity);
	}
}
