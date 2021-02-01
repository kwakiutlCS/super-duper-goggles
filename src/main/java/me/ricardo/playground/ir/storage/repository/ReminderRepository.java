package me.ricardo.playground.ir.storage.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;

@ApplicationScoped
public class ReminderRepository implements PanacheRepository<ReminderEntity> {

	public List<ReminderEntity> findByUser(String user) {
		return list("from Reminder r left join fetch r.time t where userId=?1", user);
	}
}
