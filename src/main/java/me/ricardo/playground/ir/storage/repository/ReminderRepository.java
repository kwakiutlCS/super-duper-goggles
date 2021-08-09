package me.ricardo.playground.ir.storage.repository;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;

@ApplicationScoped
public class ReminderRepository implements PanacheRepository<ReminderEntity> {

    public List<ReminderEntity> findByUser(String user) {
        return list("from Reminder r left join fetch r.time t where r.userId=?1", user);
    }
    
    public Optional<ReminderEntity> findByIdAndUser(long id, String user) {
        List<ReminderEntity> result = list("from Reminder r left join fetch r.time t where r.id=?1 and r.userId=?2", id, user);
        
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public List<ReminderEntity> findAtTimestamp(long timestamp) {
        return list("from Reminder r join fetch r.time t where t.timestamp=?1 and t.unit=null", timestamp);
    }

    public List<ReminderEntity> findRecurrentAtCurrentSecond(long seconds) {
        return list("from Reminder r join fetch r.time t where t.secondsSinceStartDay=?1", seconds);
    }
}
