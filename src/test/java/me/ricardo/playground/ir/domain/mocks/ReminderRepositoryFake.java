package me.ricardo.playground.ir.domain.mocks;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

public class ReminderRepositoryFake extends ReminderRepository {

	private HashMap<Long, ReminderEntity> reminders = new HashMap<>();
	
	private long next_id = 1L;
	
	public List<ReminderEntity> listAll() {
		return reminders.values().stream().collect(Collectors.toList());
	}
	
	public void persist(ReminderEntity entity) {
		if (entity.id == null) {
		    entity.id = next_id++;
		}
		
		reminders.put(entity.id, entity);
	}
	
	public Optional<ReminderEntity> findByIdOptional(Long id) {
		return Optional.ofNullable(reminders.get(id));
	}
	
	public boolean deleteById(Long id) {
		return reminders.remove(id) != null;
	}
}