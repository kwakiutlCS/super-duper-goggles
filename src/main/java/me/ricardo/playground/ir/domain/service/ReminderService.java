package me.ricardo.playground.ir.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.transaction.Transactional;

import me.ricardo.playground.ir.domain.adapter.ReminderAdapter;
import me.ricardo.playground.ir.domain.entity.repetion.Time;
import me.ricardo.playground.ir.storage.entity.TimeEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

@Dependent
public class ReminderService {

	private ReminderRepository reminderRepository;
	
	private ReminderCrud crud;
	
	public ReminderService(ReminderRepository reminderRepository, ReminderCrud crud) {
		this.reminderRepository = reminderRepository;
		this.crud = crud;
	}

	
	public List<Long> getSchedule(long id, String user, List<Long> interval, Long limit) {
		if (interval == null || interval.isEmpty()) {
			return List.of();
		}
		
		Stream<Long> schedule = crud.getReminder(id, user).map(r -> r.schedule(interval.get(0)))
				                                          .orElse(Stream.empty());
		
		if (interval.size() == 2) {
			schedule = schedule.takeWhile(s -> s < interval.get(1));
		}
		
		if (limit != null) {
			schedule = schedule.limit(limit);
		}
		
		return schedule.collect(Collectors.toList());
	}

	@Transactional
    public boolean addException(long id, String user, long exception) {
        Optional<TimeEntity> entityOptional = reminderRepository.findByIdOptional(id)
                                                                .filter(r -> r.userId.equals(user))
                                                                .map(r -> r.time);
        
        if (entityOptional.isEmpty()) return false;
        
        TimeEntity entity = entityOptional.get();
        
        Time time = ReminderAdapter.fromStorage(entity);
        boolean isExceptionAdded = time.addException(exception);
        
        if (isExceptionAdded) {
            ReminderAdapter.toStorage(time, entity);
        }
        
        return isExceptionAdded;
    }
}
