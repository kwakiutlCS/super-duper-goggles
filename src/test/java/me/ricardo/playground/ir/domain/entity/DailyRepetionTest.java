package me.ricardo.playground.ir.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class DailyRepetionTest {

	private static final long TIMESTAMP = 1000L;
	
	private static final long DAY = 86400;

	@Test
	public void shouldHaveUnboundedOneDayRepetion() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP));
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY, TIMESTAMP+2*DAY), reminder.schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldHaveBoundedOneDayRepetion() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 1, TIMESTAMP));
		
		// verification
		assertEquals(List.of(TIMESTAMP), reminder.schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldHaveBoundedOneDayRepetion2() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 1, TIMESTAMP + 100000L));
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY), reminder.schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldHaveMultipleDayRepetion() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 2, null));
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+ 2*DAY, TIMESTAMP+ 4*DAY), reminder.schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldOneDayRepetionFromStartingPoint() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP));
		
		// verification
		assertEquals(List.of(TIMESTAMP+DAY, TIMESTAMP+2*DAY, TIMESTAMP+3*DAY), reminder.schedule(TIMESTAMP+1).limit(3).collect(Collectors.toList()));
	}
}
