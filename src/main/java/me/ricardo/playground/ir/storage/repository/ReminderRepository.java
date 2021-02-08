package me.ricardo.playground.ir.storage.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;

@ApplicationScoped
public class ReminderRepository implements PanacheRepository<ReminderEntity> {

    public List<ReminderEntity> findByUser(String user) {
        return list("from Reminder r left join fetch r.time t where r.userId=?1", user);
    }

    public List<ReminderEntity> findAtTimestamp(long timestamp) {
        return list("from Reminder r join fetch r.time t where t.timestamp=?1 and t.unit=null", timestamp);
    }

    public List<ReminderEntity> findRecurrentAtCurrentSecond(long seconds) {
        return list("from Reminder r join fetch r.time t left join fetch t.exceptions e where t.secondsSinceStartDay=?1", seconds);
    }
}
