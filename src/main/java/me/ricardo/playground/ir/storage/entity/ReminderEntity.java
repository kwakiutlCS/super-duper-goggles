package me.ricardo.playground.ir.storage.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity(name = "Reminder")
public class ReminderEntity extends PanacheEntity {

    public String content;
    
    @Column(nullable = false, name = "user_id")
    public String userId;
    
    @Column(nullable = false, name = "created_at")
    public long createdAt;

    @Column(nullable = false, name = "updated_at")
    public long updatedAt;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "reminder", orphanRemoval = true)
    private TimeEntity time;
    
    public void setTime(TimeEntity time) {
        if (time != null) {
            time.reminder = this;
            this.time = time;
        } else {
            this.time = null;
        }
    }
    
    public TimeEntity getTime() {
        return time;
    }
}
