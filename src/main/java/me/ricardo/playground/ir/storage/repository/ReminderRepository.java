package me.ricardo.playground.ir.storage.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import me.ricardo.playground.ir.storage.entity.ReminderEntity;

@ApplicationScoped
public class ReminderRepository implements PanacheRepository<ReminderEntity> {

	public long deleteUserReminderById(long id, String user) {
		return delete("id=?1 and userId=?2", id, user);
	}

	public List<ReminderEntity> findByUser(String user) {
		return list("userId", user);
	}
}
