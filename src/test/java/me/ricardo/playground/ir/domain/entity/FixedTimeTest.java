package me.ricardo.playground.ir.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class FixedTimeTest {

	private static final long TIMESTAMP = 1000L;

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
}
