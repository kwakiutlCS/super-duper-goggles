package me.ricardo.playground.ir.domain.entity.repetion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.entity.Reminder;

public class DailyRepetionTest {

	private static final long TIMESTAMP = 1020L;
	
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
		reminder.setTime(new DailyRepetion(TIMESTAMP, 1, Bound.timestamp(TIMESTAMP), ZoneOffset.UTC));
		
		// verification
		assertEquals(List.of(TIMESTAMP), reminder.schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldHaveBoundedOneDayRepetion2() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 1, Bound.timestamp(TIMESTAMP + 100000L), ZoneOffset.UTC));
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY), reminder.schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldHaveMultipleDayRepetion() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 2, Bound.none(), ZoneOffset.UTC));
		
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
	
	@Test
	public void shouldOneDayRepetionFromStartingPoint2() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP));
		
		// verification
		assertEquals(List.of(TIMESTAMP+DAY, TIMESTAMP+2*DAY, TIMESTAMP+3*DAY), reminder.schedule(TIMESTAMP+DAY-1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldMultipleDayRepetionFromStartingPoint() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC));
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.schedule(TIMESTAMP+1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldMultipleDayRepetionFromStartingPoint2() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC));
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.schedule(TIMESTAMP+DAY-1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldMultipleDayRepetionFromStartingPoint3() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC));
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.schedule(TIMESTAMP+ 2*DAY + 1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldMultipleDayRepetionFromStartingPoint4() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC));
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.schedule(TIMESTAMP+ 2*DAY - 1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldMultipleDayRepetionFromStartingPointIntegerStep() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC));
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.schedule(TIMESTAMP+3*DAY).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldScheduleInDaySavingsChanges() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(0, 1, Bound.none(), ZoneId.of("Europe/Lisbon")));
		
		// verification
		assertEquals(1603497600, reminder.schedule(1603497600L).limit(3).collect(Collectors.toList()).get(0));
		assertEquals(1603497600+DAY, reminder.schedule(1603497600L).limit(3).collect(Collectors.toList()).get(1));
		assertEquals(1603501200+2*DAY, reminder.schedule(1603497600L).limit(3).collect(Collectors.toList()).get(2));
	}
	
	@Test
	public void shouldScheduleInDaySavingsChanges2() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(922334400, 1, Bound.none(), ZoneId.of("Europe/Lisbon")));
		
		// verification
		assertEquals(1585281600, reminder.schedule(1585281600L).limit(3).collect(Collectors.toList()).get(0));
		assertEquals(1585281600+DAY, reminder.schedule(1585281600L).limit(3).collect(Collectors.toList()).get(1));
		assertEquals(1585278000+2*DAY, reminder.schedule(1585281600L).limit(3).collect(Collectors.toList()).get(2));
	}
	
	@Test
	public void shouldTruncateTimeToMinute() {
		// data
		Reminder reminder = new Reminder("content");
		
		// verification
		reminder.setTime(new DailyRepetion(20));
		assertEquals(0, reminder.schedule().limit(1).collect(Collectors.toList()).get(0));
		
		reminder.setTime(new DailyRepetion(60));
		assertEquals(60, reminder.schedule().limit(1).collect(Collectors.toList()).get(0));
		
		reminder.setTime(new DailyRepetion(80));
		assertEquals(60, reminder.schedule().limit(1).collect(Collectors.toList()).get(0));
		
		reminder.setTime(new DailyRepetion(120));
		assertEquals(120, reminder.schedule().limit(1).collect(Collectors.toList()).get(0));
	}
	
	@Test
	public void shouldBoundScheduleByCount() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 1, Bound.count(2), ZoneOffset.UTC));
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY), reminder.schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldBoundScheduleByCount2() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 1, Bound.count(7), ZoneOffset.UTC));
		
		// verification
		assertEquals(List.of(TIMESTAMP+5*DAY, TIMESTAMP+6*DAY), reminder.schedule(TIMESTAMP+4*DAY+1).limit(10).collect(Collectors.toList()));
	}
	
	@Test
	public void shouldBoundScheduleByCount3() {
		// data
		Reminder reminder = new Reminder("content");
		reminder.setTime(new DailyRepetion(TIMESTAMP, 1, Bound.count(7), ZoneOffset.UTC));
		
		// verification
		assertEquals(List.of(TIMESTAMP+4*DAY, TIMESTAMP+5*DAY, TIMESTAMP+6*DAY), reminder.schedule(TIMESTAMP+4*DAY).limit(10).collect(Collectors.toList()));
	}
}
