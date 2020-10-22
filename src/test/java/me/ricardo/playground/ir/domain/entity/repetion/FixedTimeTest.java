package me.ricardo.playground.ir.domain.entity.repetion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.entity.Reminder;

public class FixedTimeTest {

	private static final long TIMESTAMP = 1020L;

	@Test
	public void shouldHaveOneElementSchedule() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new FixedTime(TIMESTAMP));
		
		// verification
		assertEquals(List.of(TIMESTAMP), reminder.schedule().collect(Collectors.toList()));
	}
	
	@Test
	public void shouldHaveOneElementScheduleFromOffset() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new FixedTime(TIMESTAMP));
		
		// verification
		assertEquals(List.of(TIMESTAMP), reminder.schedule(TIMESTAMP).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldHaveOneElementScheduleFromOffset2() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new FixedTime(TIMESTAMP));
		
		// verification
		assertEquals(List.of(), reminder.schedule(TIMESTAMP+1).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldTruncateTimeToMinute() {
		// data
		Reminder reminder = new Reminder("content");
		
		// verification
		reminder.setTime(new FixedTime(20));
		assertEquals(0, reminder.schedule().collect(Collectors.toList()).get(0));
		
		reminder.setTime(new FixedTime(60));
		assertEquals(60, reminder.schedule().collect(Collectors.toList()).get(0));
		
		reminder.setTime(new FixedTime(80));
		assertEquals(60, reminder.schedule().collect(Collectors.toList()).get(0));
		
		reminder.setTime(new FixedTime(120));
		assertEquals(120, reminder.schedule().collect(Collectors.toList()).get(0));
	}
}
