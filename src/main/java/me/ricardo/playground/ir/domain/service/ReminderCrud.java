package me.ricardo.playground.ir.domain.service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;

import me.ricardo.playground.ir.domain.adapter.ReminderAdapter;
import me.ricardo.playground.ir.domain.entity.Metadata;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.validation.ReminderUpdate;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

@Dependent
public class ReminderCrud {

    private ReminderRepository reminderRepository;
    
    private Clock clock;
    
    public ReminderCrud(ReminderRepository reminderRepository, Clock clock) {
        this.reminderRepository = reminderRepository;
        this.clock = clock;
    }

    @Transactional
    public Reminder createReminder(@NotNull @Valid Reminder reminder) {
        ReminderEntity entity = ReminderAdapter.toStorage(reminder, Metadata.of(clock.instant().getEpochSecond()));
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

    @Transactional
    public Optional<Reminder> updateReminder(@NotNull @Valid @ConvertGroup(from=Default.class, to=ReminderUpdate.class) Reminder reminder) {
        return reminderRepository.findByIdOptional(reminder.getId())
                                 .filter(r -> r.userId.equals(reminder.getUser()))
                                 .map(e -> ReminderAdapter.toStorage(reminder,
                                                                     Metadata.of(e.createdAt, clock.instant().getEpochSecond()),
                                                                     e))
                                 .map(ReminderAdapter::fromStorage);
    }

    @Transactional
    public boolean deleteReminder(long id, String user) {
        return reminderRepository.findByIdOptional(id)
                                 .filter(r -> r.userId.equals(user))
                                 .map(r -> reminderRepository.deleteById(r.id))
                                 .orElse(false);
    }
}
