package me.ricardo.playground.ir.domain.entity;

import java.util.stream.Stream;

import me.ricardo.playground.ir.domain.entity.repetion.Time;

public class Reminder {

	private final Long id;

	private final String user;
	
	private final String content;

	private final Metadata metadata;
	
	private final Time time;
	
	public Reminder(Long id, String user, String content, Time time, Metadata metadata) {
		this.id = id;
		this.user = user;
		this.content = content;
		this.time = time;
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

	public Stream<Long> schedule() {
		return time == null ? Stream.empty() : time.schedule();
	}
	
	public Stream<Long> schedule(long start) {
		return time == null ? Stream.empty() : time.schedule(start);
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
			builder.time = reminder.time;
			builder.metadata = reminder.metadata;
			
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
