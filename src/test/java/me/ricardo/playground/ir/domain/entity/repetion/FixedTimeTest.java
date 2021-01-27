package me.ricardo.playground.ir.domain.entity.repetion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import me.ricardo.playground.ir.domain.entity.Reminder;

class FixedTimeTest {

	private static final long TIMESTAMP = 1020L;
	
	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void shouldHaveOneElementSchedule() {
		// data
		Reminder reminder = Reminder.Builder.start()
				                            .withContent("content")
				                            .withTime(new FixedTime(TIMESTAMP))
				                            .build();
		
		// verification
		assertEquals(List.of(TIMESTAMP), reminder.getTime().schedule().collect(Collectors.toList()));
	}
	
	@Test
	void shouldHaveOneElementScheduleFromOffset() {
		// data
		Reminder reminder = Reminder.Builder.start()
							                .withContent("content")
							                .withTime(new FixedTime(TIMESTAMP))
							                .build();
		
		// verification
		assertEquals(List.of(TIMESTAMP), reminder.getTime().schedule(TIMESTAMP).collect(Collectors.toList()));
	}
	
	@Test
	void shouldHaveOneElementScheduleFromOffset2() {
// data
		Reminder reminder = Reminder.Builder.start()
							                .withContent("content")
							                .withTime(new FixedTime(TIMESTAMP))
							                .build();
		
		// verification
		assertEquals(List.of(), reminder.getTime().schedule(TIMESTAMP+1).collect(Collectors.toList()));
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
	
	@Nested
    class TimeValidation {
        @ParameterizedTest
        @ValueSource(longs = {-60, -1})
        void shouldNotAllowNegativeTime(long time) {
            // verification
            assertEquals(1, validator.validateValue(FixedTime.class, "time", time).size());
        }
    }
	
	@Nested
	class TruncateReminder {
	    @Test
	    void shouldNotBeAffectedByTruncationAfterReminderTime() {
	        // data
	        Time time = new FixedTime(TIMESTAMP);
	        
	        // action
	        Time result = time.truncate(TIMESTAMP + 1);
	    
	        // verification
	        assertEquals(time, result);
	    }
	    
	    @Test
	    void shouldNullifyReminderTruncatedBeforeReminderTime() {
	        // data
            Time time = new FixedTime(TIMESTAMP);
            
            // action
            Time result = time.truncate(TIMESTAMP);
        
            // verification
            assertEquals(NoTime.INSTANCE, result);
	    }
	}
}
