package me.ricardo.playground.ir.api.adapter;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import me.ricardo.playground.ir.api.entity.BoundDto;
import me.ricardo.playground.ir.api.entity.ReminderDto;
import me.ricardo.playground.ir.api.entity.TimeDto;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.Bound;
import me.ricardo.playground.ir.domain.entity.repetion.DailyRepetion;
import me.ricardo.playground.ir.domain.entity.repetion.FixedTime;
import me.ricardo.playground.ir.domain.entity.repetion.Time;

public class ReminderAdapter {
	
	private ReminderAdapter() { }

	public static Reminder toService(ReminderDto dto) {
		try {
			return Reminder.Builder.start()
			                       .withContent(dto.getContent())
			                       .withUser(dto.getUser())
			                       .withTime(toService(dto.getTime()))
			                       .build();
		} catch (IllegalArgumentException e) {
			throw new ConstraintViolationException(Set.of());
		}
	}
	
	private static Time toService(TimeDto dto) {
		if (dto == null) {
			return null;
		}
		
		if (dto.getUnit() == null) {
			return new FixedTime(dto.getValue());
		} else {
			ZoneId zone = dto.getZone() == null ? ZoneOffset.UTC : ZoneId.of(dto.getZone());
			return new DailyRepetion(dto.getValue(), dto.getStep(), toService(dto.getBound()), zone, dto.getExceptions());
		}
	}
	
	private static Bound toService(BoundDto dto) {
		if (dto == null) {
			return Bound.none();
		}
		
		if (dto.getLimit() != null) {
			return Bound.count(dto.getLimit());
		}
		
		if (dto.getTimestamp() != null) {
			return Bound.timestamp(dto.getTimestamp());
		}

		return Bound.none();
	}
	
	public static ReminderDto fromService(Reminder reminder) {
		ReminderDto dto = new ReminderDto();
		dto.setContent(reminder.getContent());
		
		dto.setId(reminder.getId());
		dto.setUser(reminder.getUser());
		dto.setCreatedAt(reminder.getMetadata().getCreatedAt());
		dto.setUpdatedAt(reminder.getMetadata().getUpdatedAt());
		dto.setTime(fromService(reminder.getTime()));
		
		return dto;
	}
	
	private static TimeDto fromService(Time time) {
		if (time == null) {
			return null;
		}
		
		TimeDto dto = new TimeDto();
		
		if (time instanceof FixedTime f) {
			dto.setValue(f.getTime());
			
		} else if (time instanceof DailyRepetion d) {
			dto.setValue(d.getStart());
			dto.setUnit(ChronoUnit.DAYS.name());
			dto.setStep(d.getStep());
			dto.setZone(d.getZone().getId());
			dto.setBound(fromService(d.getBound()));
			dto.setExceptions(d.getExceptions().isEmpty() ? null : d.getExceptions());
		}
		
		return dto;
	}
	
	public static BoundDto fromService(Bound bound) {
		BoundDto dto = new BoundDto();
		
		switch (bound.getType()) {
		case COUNT_BOUND:
			dto.setLimit(bound.getLimit());
			break;
			
		case NO_BOUND:
			dto = null;
			break;
			
		case TIMESTAMP_BOUND:
			dto.setTimestamp(bound.getTimestamp());
			break;
		}
		
		return dto;
	}
}
