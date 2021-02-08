package me.ricardo.playground.ir.domain.entity;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import javax.validation.groups.Default;

import me.ricardo.playground.ir.domain.entity.repetition.NoTime;
import me.ricardo.playground.ir.domain.entity.repetition.Time;
import me.ricardo.playground.ir.domain.validation.ReminderUpdate;

public class Reminder {

    @NotNull(groups = ReminderUpdate.class)
    @Positive(groups = ReminderUpdate.class)
    @Null
    private final Long id;

    @NotBlank(groups = {Default.class, ReminderUpdate.class})
    private final String user;
    
    @NotNull
    private final String content;

    private final Metadata metadata;
    
    @Valid
    private final Time time;
    
    public Reminder(Long id, String user, String content, Time time, Metadata metadata) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.time = time == null ? NoTime.INSTANCE : time;
        this.metadata = metadata;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getUser() {
        return user;
    }
    
    public Metadata getMetadata() {
        return metadata;
    }
    
    public Time getTime() {
        return time;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Reminder)) {
            return false;
        }
        Reminder castOther = (Reminder) other;
        return Objects.equals(id, castOther.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    

    public static class Builder {
        private Long id;

        private String user;
        
        private String content;

        private Metadata metadata;
        
        private Time time;
        
        private Builder() {}
        
        public static Builder start() {
            return new Builder();
        }
        
        public static Builder start(Reminder reminder) {
            Builder builder = new Builder();
            builder.id = reminder.id;
            builder.user = reminder.user;
            builder.content = reminder.content;
            builder.metadata = reminder.metadata;
            builder.time = reminder.time;
            
            return builder;
        }
        
        public Reminder build() {
            return new Reminder(this.id, this.user, this.content, this.time, this.metadata);
        }
        
        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withUser(String user) {
            this.user = user;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder withTime(Time time) {
            this.time = time;
            return this;
        }
    }
}
