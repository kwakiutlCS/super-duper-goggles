package me.ricardo.playground.ir.domain.entity.repetion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.entity.Reminder;

class DailyRepetionTest {

	private static final long TIMESTAMP = 1020L;
	
	private static final long DAY = 86400;

	@Test
	void shouldHaveUnboundedOneDayRepetion() {
		// data
		Time time = new DailyRepetion(TIMESTAMP);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY, TIMESTAMP+2*DAY), reminder.schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldHaveBoundedOneDayRepetion() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 1, Bound.timestamp(TIMESTAMP), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP), reminder.schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldHaveBoundedOneDayRepetion2() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 1, Bound.timestamp(TIMESTAMP + 100000L), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY), reminder.schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldHaveMultipleDayRepetion() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 2, Bound.none(), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+ 2*DAY, TIMESTAMP+ 4*DAY), reminder.schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldOneDayRepetionFromStartingPoint() {
		// data
		Time time = new DailyRepetion(TIMESTAMP);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+DAY, TIMESTAMP+2*DAY, TIMESTAMP+3*DAY), reminder.schedule(TIMESTAMP+1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldOneDayRepetionFromStartingPoint2() {
		// data
		Time time = new DailyRepetion(TIMESTAMP);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+DAY, TIMESTAMP+2*DAY, TIMESTAMP+3*DAY), reminder.schedule(TIMESTAMP+DAY-1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldMultipleDayRepetionFromStartingPoint() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.schedule(TIMESTAMP+1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldMultipleDayRepetionFromStartingPoint2() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.schedule(TIMESTAMP+DAY-1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldMultipleDayRepetionFromStartingPoint3() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.schedule(TIMESTAMP+ 2*DAY + 1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldMultipleDayRepetionFromStartingPoint4() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.schedule(TIMESTAMP+ 2*DAY - 1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldMultipleDayRepetionFromStartingPointIntegerStep() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.schedule(TIMESTAMP+3*DAY).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldScheduleInDaySavingsChanges() {
		// data
		Time time = new DailyRepetion(0, 1, Bound.none(), ZoneId.of("Europe/Lisbon"));
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(1603497600, reminder.schedule(1603497600L).limit(3).collect(Collectors.toList()).get(0));
		assertEquals(1603497600+DAY, reminder.schedule(1603497600L).limit(3).collect(Collectors.toList()).get(1));
		assertEquals(1603501200+2*DAY, reminder.schedule(1603497600L).limit(3).collect(Collectors.toList()).get(2));
	}
	
	@Test
	void shouldScheduleInDaySavingsChanges2() {
		// data
		Time time = new DailyRepetion(922334400, 1, Bound.none(), ZoneId.of("Europe/Lisbon"));
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(1585281600, reminder.schedule(1585281600L).limit(3).collect(Collectors.toList()).get(0));
		assertEquals(1585281600+DAY, reminder.schedule(1585281600L).limit(3).collect(Collectors.toList()).get(1));
		assertEquals(1585278000+2*DAY, reminder.schedule(1585281600L).limit(3).collect(Collectors.toList()).get(2));
	}
	
	@Test
	void shouldScheduleInDaySavingsChanges3() {
		// data
		Time time = new DailyRepetion(1601514120, 1, Bound.none(), ZoneId.of("Europe/Lisbon"));
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(1603591320, reminder.schedule(1603591260).limit(3).collect(Collectors.toList()).get(0));
	}
	
	@Test
	void shouldTruncateTimeToMinute() {
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
	
	@Test
	void shouldBoundScheduleByCount() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 1, Bound.count(2), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY), reminder.schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldBoundScheduleByCount2() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 1, Bound.count(7), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+5*DAY, TIMESTAMP+6*DAY), reminder.schedule(TIMESTAMP+4*DAY+1).limit(10).collect(Collectors.toList()));
	}
	
	@Test
	void shouldBoundScheduleByCount3() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 1, Bound.count(7), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+4*DAY, TIMESTAMP+5*DAY, TIMESTAMP+6*DAY), reminder.schedule(TIMESTAMP+4*DAY).limit(10).collect(Collectors.toList()));
	}
	
	@Test
	void shouldBoundScheduleByCount4() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 3, Bound.count(3), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(), reminder.schedule(TIMESTAMP+7*DAY).limit(10).collect(Collectors.toList()));
	}
	
	@Test
	void shouldAllowCancelSpecificIterationOfReminder() {
		// data
		DailyRepetion time = new DailyRepetion(TIMESTAMP, 1, Bound.count(7), ZoneOffset.UTC, Set.of(2*DAY+TIMESTAMP, 3*DAY+TIMESTAMP, 5*DAY+TIMESTAMP));
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+4*DAY, TIMESTAMP+6*DAY), reminder.schedule(TIMESTAMP+2*DAY).limit(10).collect(Collectors.toList()));
	}
	
	@Test
	void shouldAllowCancelSpecificIterationOfReminder2() {
		// data
		DailyRepetion time = new DailyRepetion(TIMESTAMP, 1, Bound.count(7), ZoneId.of("Europe/Lisbon"), Set.of(2*DAY+TIMESTAMP, 3*DAY+TIMESTAMP, 5*DAY+TIMESTAMP));
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+4*DAY, TIMESTAMP+6*DAY), reminder.schedule(TIMESTAMP+2*DAY).limit(10).collect(Collectors.toList()));
	}
}
