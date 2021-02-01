package me.ricardo.playground.ir.domain.entity.repetion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.repetion.Bound.BoundType;

class DailyRepetionTest {

	private static final long TIMESTAMP = 1020L;
	
	private static final long DAY = 86400;
	
	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void shouldHaveUnboundedOneDayRepetion() {
		// data
		Time time = new DailyRepetion(TIMESTAMP);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY, TIMESTAMP+2*DAY), reminder.getTime().schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldHaveExceptionsToSchedule() {
	    // data
	    Time time = new DailyRepetion(TIMESTAMP, 1, Bound.count(3), ZoneOffset.UTC, Set.of(TIMESTAMP));
	    Reminder reminder = Reminder.Builder.start().withTime(time).build();
	    
	    // verification
	    assertEquals(List.of(TIMESTAMP + DAY), reminder.getTime().schedule().limit(1).collect(Collectors.toList()));
	}
	
	@Test
	void shouldHaveBoundedOneDayRepetion() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 1, Bound.timestamp(TIMESTAMP), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP), reminder.getTime().schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldHaveBoundedOneDayRepetion2() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 1, Bound.timestamp(TIMESTAMP + 100000L), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY), reminder.getTime().schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldHaveMultipleDayRepetion() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 2, Bound.none(), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+ 2*DAY, TIMESTAMP+ 4*DAY), reminder.getTime().schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldOneDayRepetionFromStartingPoint() {
		// data
		Time time = new DailyRepetion(TIMESTAMP);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+DAY, TIMESTAMP+2*DAY, TIMESTAMP+3*DAY), reminder.getTime().schedule(TIMESTAMP+1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldOneDayRepetionFromStartingPoint2() {
		// data
		Time time = new DailyRepetion(TIMESTAMP);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+DAY, TIMESTAMP+2*DAY, TIMESTAMP+3*DAY), reminder.getTime().schedule(TIMESTAMP+DAY-1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldMultipleDayRepetionFromStartingPoint() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.getTime().schedule(TIMESTAMP+1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldMultipleDayRepetionFromStartingPoint2() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.getTime().schedule(TIMESTAMP+DAY-1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldMultipleDayRepetionFromStartingPoint3() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.getTime().schedule(TIMESTAMP+ 2*DAY + 1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldMultipleDayRepetionFromStartingPoint4() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.getTime().schedule(TIMESTAMP+ 2*DAY - 1).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldMultipleDayRepetionFromStartingPointIntegerStep() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.getTime().schedule(TIMESTAMP+3*DAY).limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldScheduleInDaySavingsChanges() {
		// data
		Time time = new DailyRepetion(0, 1, Bound.none(), ZoneId.of("Europe/Lisbon"));
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(1603497600, reminder.getTime().schedule(1603497600L).limit(3).collect(Collectors.toList()).get(0));
		assertEquals(1603497600+DAY, reminder.getTime().schedule(1603497600L).limit(3).collect(Collectors.toList()).get(1));
		assertEquals(1603501200+2*DAY, reminder.getTime().schedule(1603497600L).limit(3).collect(Collectors.toList()).get(2));
	}
	
	@Test
	void shouldScheduleInDaySavingsChanges2() {
		// data
		Time time = new DailyRepetion(922334400, 1, Bound.none(), ZoneId.of("Europe/Lisbon"));
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(1585281600, reminder.getTime().schedule(1585281600L).limit(3).collect(Collectors.toList()).get(0));
		assertEquals(1585281600+DAY, reminder.getTime().schedule(1585281600L).limit(3).collect(Collectors.toList()).get(1));
		assertEquals(1585278000+2*DAY, reminder.getTime().schedule(1585281600L).limit(3).collect(Collectors.toList()).get(2));
	}
	
	@Test
	void shouldScheduleInDaySavingsChanges3() {
		// data
		Time time = new DailyRepetion(1601514120, 1, Bound.none(), ZoneId.of("Europe/Lisbon"));
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(1603591320, reminder.getTime().schedule(1603591260).limit(3).collect(Collectors.toList()).get(0));
	}
	
	@Test
	void shouldTruncateTimeToMinute() {
		// data
		Reminder.Builder builder = Reminder.Builder.start();
		
		// verification
		Reminder r1 = builder.withTime(new FixedTime(20)).build();
		assertEquals(0, r1.getTime().schedule().collect(Collectors.toList()).get(0));
		
		Reminder r2 = builder.withTime(new FixedTime(60)).build();
		assertEquals(60, r2.getTime().schedule().collect(Collectors.toList()).get(0));
		
		Reminder r3 = builder.withTime(new FixedTime(80)).build();
		assertEquals(60, r3.getTime().schedule().collect(Collectors.toList()).get(0));
		
		Reminder r4 = builder.withTime(new FixedTime(120)).build();
		assertEquals(120, r4.getTime().schedule().collect(Collectors.toList()).get(0));
	}
	
	@Test
	void shouldBoundScheduleByCount() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 1, Bound.count(2), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY), reminder.getTime().schedule().limit(3).collect(Collectors.toList()));
	}
	
	@Test
	void shouldBoundScheduleByCount2() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 1, Bound.count(7), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+5*DAY, TIMESTAMP+6*DAY), reminder.getTime().schedule(TIMESTAMP+4*DAY+1).limit(10).collect(Collectors.toList()));
	}
	
	@Test
	void shouldBoundScheduleByCount3() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 1, Bound.count(7), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+4*DAY, TIMESTAMP+5*DAY, TIMESTAMP+6*DAY), reminder.getTime().schedule(TIMESTAMP+4*DAY).limit(10).collect(Collectors.toList()));
	}
	
	@Test
	void shouldBoundScheduleByCount4() {
		// data
		Time time = new DailyRepetion(TIMESTAMP, 3, Bound.count(3), ZoneOffset.UTC);
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(), reminder.getTime().schedule(TIMESTAMP+7*DAY).limit(10).collect(Collectors.toList()));
	}
	
	@Test
	void shouldAllowCancelSpecificIterationOfReminder() {
		// data
		DailyRepetion time = new DailyRepetion(TIMESTAMP, 1, Bound.count(7), ZoneOffset.UTC, Set.of(2*DAY+TIMESTAMP, 3*DAY+TIMESTAMP, 5*DAY+TIMESTAMP));
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+4*DAY, TIMESTAMP+6*DAY), reminder.getTime().schedule(TIMESTAMP+2*DAY).limit(10).collect(Collectors.toList()));
	}
	
	@Test
	void shouldAllowCancelSpecificIterationOfReminder2() {
		// data
		DailyRepetion time = new DailyRepetion(TIMESTAMP, 1, Bound.count(7), ZoneId.of("Europe/Lisbon"), Set.of(2*DAY+TIMESTAMP, 3*DAY+TIMESTAMP, 5*DAY+TIMESTAMP));
		Reminder reminder = Reminder.Builder.start().withTime(time).build();
		
		// verification
		assertEquals(List.of(TIMESTAMP+4*DAY, TIMESTAMP+6*DAY), reminder.getTime().schedule(TIMESTAMP+2*DAY).limit(10).collect(Collectors.toList()));
	}
	
	@Test
	void shouldAddAndRetrieveExceptionsToDailyReminder() {
		// data
		DailyRepetion time = new DailyRepetion(60L, 1, Bound.count(3L), ZoneOffset.UTC, Set.of(60L + ChronoUnit.DAYS.getDuration().getSeconds()));
		
		// verification
		assertEquals(86460, time.getExceptions().toArray(new Long[1])[0]);
	}
	
	@Test
	void shouldAddAndRetrieveExceptionsToDailyReminder2() {
		// data
		DailyRepetion time = new DailyRepetion(60L, 1, Bound.count(3L), ZoneOffset.UTC, Set.of(0L, 60L + ChronoUnit.DAYS.getDuration().getSeconds(), 999999L));
		
		// verification
		assertEquals(86460, time.getExceptions().toArray(new Long[1])[0]);
	}
	
	@Test
	void shouldNotAllowExceptionsIfNotPreviouslyOccurring() {
		// data
		DailyRepetion time = new DailyRepetion(60L, 1, Bound.count(3L), ZoneOffset.UTC, Set.of(9999999999L));
		
		// verification
		assertEquals(0, time.getExceptions().size());
	}
	
	@Test
	void shouldAllowNullException() {
        // data
		DailyRepetion time = new DailyRepetion(60L, 1, Bound.count(3L), ZoneOffset.UTC, null);
		
		// verication
		assertTrue(time.getExceptions().isEmpty());
	}
	
	@Nested
	class AddException {
	    @Test
	    void shouldAllowAddingException() {
	        // data
	        DailyRepetion time = new DailyRepetion(60L, 1, Bound.count(3L), ZoneOffset.UTC);
	        
	        // action
	        boolean result = time.addException(60L);
	        
	        // verication
	        assertTrue(result);
	        assertEquals(Set.of(60L), time.getExceptions());
	    }
	    
	    @Test
	    void shouldNotAllowAddingInexistentExceptions() {
	         // data
            DailyRepetion time = new DailyRepetion(60L, 1, Bound.count(3L), ZoneOffset.UTC);
            
            // action
            boolean result = time.addException(0L);
            
            // verication
            assertFalse(result);
            assertEquals(Set.of(), time.getExceptions());
	    }
	}
	
	@Nested
	class AddBounding {
	    @ParameterizedTest
	    @ValueSource(longs = {0, -1, -10})
	    void shouldNotAllowNonPositiveLimitBound(long limit) {
	        // verification
	        assertEquals(1, validator.validateValue(DailyRepetion.class, "bound", Bound.count(limit)).size());
	    }
	    
	    @ParameterizedTest
        @ValueSource(longs = {0, -1, -10})
        void shouldNotAllowNonPositiveTimestampBound(long timestamp) {
            // verification
            assertEquals(1, validator.validateValue(DailyRepetion.class, "bound", Bound.timestamp(timestamp)).size());
        }
	    
	    @Test
        void shouldNotAllowValuesForNoBound() {
            // verification
            assertEquals(1, validator.validateValue(DailyRepetion.class, "bound", new Bound(BoundType.NO_BOUND, 0, 1)).size());
            assertEquals(1, validator.validateValue(DailyRepetion.class, "bound", new Bound(BoundType.NO_BOUND, 1, 0)).size());
        }
	    
	    @ParameterizedTest
        @ValueSource(longs = {3600, 3000})
        void shouldNotAllowBoundTimestampSmallerOrEqualThanStart(long timestamp) {
	        // data
	        DailyRepetion time = new DailyRepetion(3600L, 1, Bound.timestamp(timestamp), ZoneOffset.UTC);
	        
            // verification
            assertEquals(1, validator.validate(time).size());
        }
	}
	
	@Nested
	class AddStart {
	    @ParameterizedTest
        @ValueSource(longs = {-60, -1})
        void shouldNotAllowNegativeStart(long start) {
            // verification
            assertEquals(1, validator.validateValue(DailyRepetion.class, "start", start).size());
        }
	}
	
	@Nested
    class AddStep {
	    @ParameterizedTest
        @ValueSource(ints = {0, -1, -10})
        void shouldNotAllowNonPositiveStep(int step) {
            // verification
            assertEquals(1, validator.validateValue(DailyRepetion.class, "step", step).size());
        }
    }
	
	@Nested
	class Truncate {
	    @Test
	    void shouldRemoveIfTruncationIsBeforeStart() {
	        // data
	        Time time = new DailyRepetion(3600L, 1, Bound.none(), ZoneOffset.UTC);
	        
	        // verification
	        assertEquals(NoTime.INSTANCE, time.truncate(3600L));
	    }
	    
	    @Test
        void shouldTruncate() {
            // data
            Time time = new DailyRepetion(3600L, 1, Bound.none(), ZoneOffset.UTC);
            
            // action
            Time result1 = time.truncate(3600L + DAY);
            Time result2 = time.truncate(3600L + DAY + 1);
            
            // verification
            assertEquals(List.of(3600L), result1.schedule().collect(Collectors.toList()));
            assertEquals(List.of(3600L, 3600L + DAY), result2.schedule().collect(Collectors.toList()));
        }
	    
	    @Test
	    void shouldRemoveExtraExceptions() {
	        // data
            DailyRepetion time = new DailyRepetion(3600L, 1, Bound.none(), ZoneOffset.UTC, Set.of(3600L + DAY, 3600L + 3 * DAY));
            
            // action
            Time result = time.truncate(2 * DAY);
            
            // verification
            assertEquals(time.getStart(), ((DailyRepetion) result).getStart());
            assertEquals(time.getStep(), ((DailyRepetion) result).getStep());
            assertEquals(time.getZone(), ((DailyRepetion) result).getZone());
            assertEquals(Set.of(3600L + DAY), ((DailyRepetion) result).getExceptions());
	    }
	}
}
