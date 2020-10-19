package me.ricardo.playground.ir.storage.repositories;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import me.ricardo.playground.ir.storage.entities.ReminderEntity;

@ApplicationScoped
public class ReminderRepository implements PanacheRepository<ReminderEntity> {

}
