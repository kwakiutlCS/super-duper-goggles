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
		Reminder reminder = Reminder.Builder.start()
				                            .withContent("content")
				                            .withTime(new FixedTime(TIMESTAMP))
				                            .build();
		
		// verification
		assertEquals(List.of(TIMESTAMP), reminder.schedule().collect(Collectors.toList()));
	}
	
	@Test
	public void shouldHaveOneElementScheduleFromOffset() {
		// data
		Reminder reminder = Reminder.Builder.start()
							                .withContent("content")
							                .withTime(new FixedTime(TIMESTAMP))
							                .build();
		
		// verification
		assertEquals(List.of(TIMESTAMP), reminder.schedule(TIMESTAMP).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldHaveOneElementScheduleFromOffset2() {
		// data
		Reminder reminder = Reminder.Builder.start()
							                .withContent("content")
							                .withTime(new FixedTime(TIMESTAMP))
							                .build();
		
		// verification
		assertEquals(List.of(), reminder.schedule(TIMESTAMP+1).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldTruncateTimeToMinute() {
		// data
		Reminder.Builder builder = Reminder.Builder.start();
		
		// verification
		Reminder r1 = builder.withTime(new FixedTime(20)).build();
		assertEquals(0, r1.schedule().collect(Collectors.toList()).get(0));
		
		Reminder r2 = builder.withTime(new FixedTime(60)).build();
		assertEquals(60, r2.schedule().collect(Collectors.toList()).get(0));
		
		Reminder r3 = builder.withTime(new FixedTime(80)).build();
		assertEquals(60, r3.schedule().collect(Collectors.toList()).get(0));
		
		Reminder r4 = builder.withTime(new FixedTime(120)).build();
		assertEquals(120, r4.schedule().collect(Collectors.toList()).get(0));
	}
}
