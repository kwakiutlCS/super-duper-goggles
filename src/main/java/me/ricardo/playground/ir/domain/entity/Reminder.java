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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reminder other = (Reminder) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (metadata == null) {
			if (other.metadata != null)
				return false;
		} else if (!metadata.equals(other.metadata))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
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
