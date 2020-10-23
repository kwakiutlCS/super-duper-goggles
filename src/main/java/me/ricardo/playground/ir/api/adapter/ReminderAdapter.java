package me.ricardo.playground.ir.api.adapter;

import me.ricardo.playground.ir.api.entity.ReminderDto;
import me.ricardo.playground.ir.api.entity.TimeDto;
import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.FixedTime;
import me.ricardo.playground.ir.domain.entity.repetion.Time;

public class ReminderAdapter {

	public static Reminder toService(ReminderDto dto) {
		Reminder reminder = new Reminder(dto.getContent());
		
		reminder.setTime(toService(dto.getTime()));
		
		return reminder;
	}
	
	private static FixedTime toService(TimeDto dto) {
		if (dto == null) {
			return null;
		}
		
		return new FixedTime(dto.getValue());
	}
	
	public static ReminderDto fromService(Reminder reminder) {
		ReminderDto dto = new ReminderDto(reminder.getContent());
		
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
		
		return new TimeDto(((FixedTime) time).getTime());
	}
}
