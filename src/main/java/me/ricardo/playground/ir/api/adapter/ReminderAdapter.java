package me.ricardo.playground.ir.api.adapter;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

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
			return new DailyRepetion(dto.getValue(), dto.getStep(), Bound.none(), zone);
		}
	}
	
	public static ReminderDto fromService(Reminder reminder) {
		ReminderDto dto = new ReminderDto();
		dto.setContent(reminder.getContent());
		
		dto.setId(reminder.getId());
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
		}
		
		return dto;
	}
}
