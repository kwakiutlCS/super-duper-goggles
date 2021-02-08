package me.ricardo.playground.ir.domain.doubles;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import me.ricardo.playground.ir.storage.entity.ReminderEntity;
import me.ricardo.playground.ir.storage.repository.ReminderRepository;

public class ReminderRepositoryFake extends ReminderRepository {

    private HashMap<Long, ReminderEntity> reminders = new HashMap<>();
    
    private long next_id = 1L;
    
    public ReminderRepositoryFake() { }
    
    public ReminderRepositoryFake(ReminderEntity... reminders) {
        for (ReminderEntity reminder : reminders) {
            persist(reminder);
        }
    }
    
    public List<ReminderEntity> findByUser(String user) {
        return reminders.values().stream()
                                 .filter(r -> user.equals(r.userId))
                                 .collect(Collectors.toList());
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
    
    public List<ReminderEntity> findAtTimestamp(long timestamp) {
        return reminders.values().stream()
                                 .filter(r -> r.time != null)
                                 .filter(r -> r.time.getTimestamp() == timestamp)
                                 .filter(r -> r.time.unit == null)
                                 .collect(Collectors.toList());
    }
    
    public List<ReminderEntity> findRecurrentAtCurrentSecond(long minute) {
        return reminders.values().stream()
                                 .filter(r -> r.time != null)
                                 .filter(r -> r.time.unit != null)
                                 .filter(r -> r.time.getTimestamp() % 86400 == minute)
                                 .collect(Collectors.toList());
    }
    
    public static ReminderRepository getNoDelete() {
        return new NoDeleteRepository();
    }
    
    static class NoDeleteRepository extends ReminderRepositoryFake {
        public boolean deleteById(Long id) {
            return false;
        }
    }
}
