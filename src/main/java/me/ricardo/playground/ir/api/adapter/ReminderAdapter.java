package me.ricardo.playground.ir.api.adapter;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import me.ricardo.playground.ir.api.entity.BoundDto;
import me.ricardo.playground.ir.api.entity.ReminderDto;
import me.ricardo.playground.ir.api.entity.TimeDto;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.Bound;
import me.ricardo.playground.ir.domain.entity.repetion.DailyRepetion;
import me.ricardo.playground.ir.domain.entity.repetion.FixedTime;
import me.ricardo.playground.ir.domain.entity.repetion.Time;

public class ReminderAdapter {

	public static Reminder toService(ReminderDto dto) {
		Reminder reminder = new Reminder(dto.getContent());
		
		reminder.setUser(dto.getUser());
		reminder.setTime(toService(dto.getTime()));
		
		return reminder;
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
		dto.setCreatedAt(reminder.getCreatedAt());
		dto.setUpdatedAt(reminder.getUpdatedAt());
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
			dto.setUnit(ChronoUnit.DAYS);
			dto.setStep(d.getStep());
			dto.setZone(d.getZone().getId());
			dto.setBound(fromService(d.getBound()));
			dto.setExceptions(d.getExceptions());
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
