package me.ricardo.playground.ir.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import me.ricardo.playground.ir.domain.adapter.ReminderAdapter;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.bound.GuaranteedBound;
import me.ricardo.playground.ir.domain.entity.repetition.NoTime;
import me.ricardo.playground.ir.domain.entity.repetition.Time;
import me.ricardo.playground.ir.domain.operator.Field;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.entity.TimeEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

@Dependent
public class ReminderService {
    
    public final ReminderCrud crud;

	private final ReminderRepository reminderRepository;
	
	public ReminderService(ReminderRepository reminderRepository, ReminderCrud crud) {
		this.reminderRepository = reminderRepository;
		this.crud = crud;
	}

	
	public List<Long> getSchedule(long id, String user, long start, @NotNull @Valid GuaranteedBound bound) {
		return reminderRepository.findByIdOptional(id)
		                         .filter(r -> r.userId.equals(user))
		                         .map(e -> ReminderAdapter.fromStorage(e, Set.of(Field.EXCEPTIONS)))
		                         .map(Reminder::getTime)
		                         .map(t -> t.schedule(start, bound))
		                         .orElse(Stream.empty())
		                         .collect(Collectors.toList());
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


	@Transactional
    public Optional<Reminder> truncate(long id, String user, long timestamp) {
        Optional<ReminderEntity> reminderEntity = reminderRepository.findByIdOptional(id)
                                                                    .filter(e -> e.userId.equals(user));
        
        Optional<Reminder> reminder = reminderEntity.map(ReminderAdapter::fromStorage);
        
        Time time = reminder.map(Reminder::getTime)
                            .orElse(NoTime.INSTANCE);
        
        if (time == NoTime.INSTANCE) {
            return reminder;
        }
        
        Time truncated = time.truncate(timestamp);
        
        if (truncated == NoTime.INSTANCE) {
            reminder = Optional.empty();
            crud.deleteReminder(id, user);
        } else {
            reminder = Optional.of(Reminder.Builder.start(reminder.orElseThrow()).withTime(truncated).build());
            ReminderAdapter.toStorage(truncated, reminderEntity.map(r -> r.time).orElseThrow());
        }
        
        return reminder;
    }
}
