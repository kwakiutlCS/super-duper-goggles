package me.ricardo.playground.ir.domain.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;

import me.ricardo.playground.ir.domain.adapter.ReminderAdapter;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.bound.Bound;
import me.ricardo.playground.ir.domain.operator.Field;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

@Dependent
public class ReminderQuerier {

    private static final int SECONDS_IN_HOUR = 3600;
    
    private static final int SECONDS_IN_DAY = 86400;

    private final ReminderRepository repository;

    public ReminderQuerier(ReminderRepository repository) {
        this.repository = repository;
    }

    public List<Reminder> findAtTimestamp(long timestamp) {
        return repository.findAtTimestamp(timestamp)
                         .stream()
                         .map(ReminderAdapter::fromStorage)
                         .collect(Collectors.toList());
    }

    public List<Reminder> findRecurrentAtTimestamp(long timestamp) {
        return Stream.concat(findRecurrentAtSecond(timestamp % SECONDS_IN_DAY),
                             findRecurrentAtSecond((timestamp + SECONDS_IN_HOUR) % SECONDS_IN_DAY)) // search also 1 hour later to find reminders created in day light saving period
                     .map(e -> ReminderAdapter.fromStorage(e, Set.of(Field.EXCEPTIONS)))
                     .filter(r -> r.getTime().schedule(timestamp, Bound.timestamp(timestamp)).findAny().isPresent())
                     .collect(Collectors.toList());
    }
    
    private Stream<ReminderEntity> findRecurrentAtSecond(long secondsSinceStartDay) {
        return repository.findRecurrentAtCurrentSecond(secondsSinceStartDay).stream();
    }
}
