package me.ricardo.playground.ir.domain.entity.repetition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import me.ricardo.playground.ir.domain.entity.Reminder;
import me.ricardo.playground.ir.domain.entity.bound.Bound;


class DailyRepetitionTest {

    private static final long TIMESTAMP = 1020L;
    
    private static final long DAY = 86400;
    
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldHaveUnboundedOneDayRepetion() {
        // data
        Time time = new DailyRepetition(TIMESTAMP);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY, TIMESTAMP+2*DAY), reminder.getTime().schedule(0, Bound.count(3)).collect(Collectors.toList()));
    }
    
    @Test
    void shouldHaveExceptionsToSchedule() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 1, Bound.count(3), ZoneOffset.UTC, Set.of(TIMESTAMP));
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP + DAY), reminder.getTime().schedule(0, Bound.count(3)).limit(1).collect(Collectors.toList()));
    }
    
    @Test
    void shouldHaveBedOneDayRepetion() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 1, Bound.timestamp(TIMESTAMP), ZoneOffset.UTC);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP), reminder.getTime().schedule(0, Bound.count(3)).collect(Collectors.toList()));
    }
    
    @Test
    void shouldHaveBedOneDayRepetion2() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 1, Bound.timestamp(TIMESTAMP + 100000L), ZoneOffset.UTC);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY), reminder.getTime().schedule(0, Bound.count(3)).collect(Collectors.toList()));
    }
    
    @Test
    void shouldHaveMultipleDayRepetion() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 2, Bound.none(), ZoneOffset.UTC);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP, TIMESTAMP+ 2*DAY, TIMESTAMP+ 4*DAY), reminder.getTime().schedule(0, Bound.count(3)).collect(Collectors.toList()));
    }
    
    @Test
    void shouldOneDayRepetionFromStartingPoint() {
        // data
        Time time = new DailyRepetition(TIMESTAMP);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP+DAY, TIMESTAMP+2*DAY, TIMESTAMP+3*DAY), reminder.getTime().schedule(TIMESTAMP+1, Bound.count(Integer.MAX_VALUE)).limit(3).collect(Collectors.toList()));
    }
    
    @Test
    void shouldOneDayRepetionFromStartingPoint2() {
        // data
        Time time = new DailyRepetition(TIMESTAMP);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP+DAY, TIMESTAMP+2*DAY, TIMESTAMP+3*DAY), reminder.getTime().schedule(TIMESTAMP+DAY-1, Bound.count(Integer.MAX_VALUE)).limit(3).collect(Collectors.toList()));
    }
    
    @Test
    void shouldMultipleDayRepetionFromStartingPoint() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.getTime().schedule(TIMESTAMP+1, Bound.count(Integer.MAX_VALUE)).limit(3).collect(Collectors.toList()));
    }
    
    @Test
    void shouldMultipleDayRepetionFromStartingPoint2() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.getTime().schedule(TIMESTAMP+DAY-1, Bound.count(Integer.MAX_VALUE)).limit(3).collect(Collectors.toList()));
    }
    
    @Test
    void shouldMultipleDayRepetionFromStartingPoint3() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.getTime().schedule(TIMESTAMP+ 2*DAY + 1, Bound.count(Integer.MAX_VALUE)).limit(3).collect(Collectors.toList()));
    }
    
    @Test
    void shouldMultipleDayRepetionFromStartingPoint4() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.getTime().schedule(TIMESTAMP+ 2*DAY - 1, Bound.count(Integer.MAX_VALUE)).limit(3).collect(Collectors.toList()));
    }
    
    @Test
    void shouldMultipleDayRepetionFromStartingPointIntegerStep() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 3, Bound.none(), ZoneOffset.UTC);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP+3*DAY, TIMESTAMP+6*DAY, TIMESTAMP+9*DAY), reminder.getTime().schedule(TIMESTAMP+3*DAY, Bound.count(Integer.MAX_VALUE)).limit(3).collect(Collectors.toList()));
    }
    
    @Test
    void shouldScheduleInDaySavingsChanges() {
        // data
        Time time = new DailyRepetition(0, 1, Bound.none(), ZoneId.of("Europe/Lisbon"));
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(1603497600, reminder.getTime().schedule(1603497600L, Bound.count(Integer.MAX_VALUE)).limit(3).collect(Collectors.toList()).get(0));
        assertEquals(1603497600+DAY, reminder.getTime().schedule(1603497600L, Bound.count(Integer.MAX_VALUE)).limit(3).collect(Collectors.toList()).get(1));
        assertEquals(1603501200+2*DAY, reminder.getTime().schedule(1603497600L, Bound.count(Integer.MAX_VALUE)).limit(3).collect(Collectors.toList()).get(2));
    }
    
    @Test
    void shouldScheduleInDaySavingsChanges2() {
        // data
        Time time = new DailyRepetition(922334400, 1, Bound.none(), ZoneId.of("Europe/Lisbon"));
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(1585281600, reminder.getTime().schedule(1585281600L, Bound.count(Integer.MAX_VALUE)).limit(1).collect(Collectors.toList()).get(0));
        assertEquals(1585281600+DAY, reminder.getTime().schedule(1585281600L, Bound.count(Integer.MAX_VALUE)).limit(2).collect(Collectors.toList()).get(1));
        assertEquals(1585278000+2*DAY, reminder.getTime().schedule(1585281600L, Bound.count(Integer.MAX_VALUE)).limit(3).collect(Collectors.toList()).get(2));
    }
    
    @Test
    void shouldScheduleInDaySavingsChanges3() {
        // data
        Time time = new DailyRepetition(1601514120, 1, Bound.none(), ZoneId.of("Europe/Lisbon"));
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(1603591320, reminder.getTime().schedule(1603591260, Bound.count(Integer.MAX_VALUE)).limit(1).collect(Collectors.toList()).get(0));
    }
    
    @Test
    void shouldTruncateTimeToMinute() {
        // data
        Reminder.Builder builder = Reminder.Builder.start();
        
        // verification
        Reminder r1 = builder.withTime(new FixedTime(20)).build();
        assertEquals(0, r1.getTime().schedule(0L, Bound.count(2)).collect(Collectors.toList()).get(0));
        
        Reminder r2 = builder.withTime(new FixedTime(60)).build();
        assertEquals(60, r2.getTime().schedule(0L, Bound.count(2)).collect(Collectors.toList()).get(0));
        
        Reminder r3 = builder.withTime(new FixedTime(80)).build();
        assertEquals(60, r3.getTime().schedule(0L, Bound.count(2)).collect(Collectors.toList()).get(0));
        
        Reminder r4 = builder.withTime(new FixedTime(120)).build();
        assertEquals(120, r4.getTime().schedule(0L, Bound.count(2)).collect(Collectors.toList()).get(0));
    }
    
    @Test
    void shouldBScheduleByCount() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 1, Bound.count(2), ZoneOffset.UTC);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP, TIMESTAMP+DAY), reminder.getTime().schedule(0L, Bound.count(3)).collect(Collectors.toList()));
    }
    
    @Test
    void shouldBScheduleByCount2() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 1, Bound.count(7), ZoneOffset.UTC);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP+5*DAY, TIMESTAMP+6*DAY), reminder.getTime().schedule(TIMESTAMP+4*DAY+1, Bound.count(10)).collect(Collectors.toList()));
    }
    
    @Test
    void shouldBScheduleByCount3() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 1, Bound.count(7), ZoneOffset.UTC);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP+4*DAY, TIMESTAMP+5*DAY, TIMESTAMP+6*DAY), reminder.getTime().schedule(TIMESTAMP+4*DAY, Bound.count(10)).collect(Collectors.toList()));
    }
    
    @Test
    void shouldBScheduleByCount4() {
        // data
        Time time = new DailyRepetition(TIMESTAMP, 3, Bound.count(3), ZoneOffset.UTC);
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(), reminder.getTime().schedule(TIMESTAMP+7*DAY, Bound.count(10)).collect(Collectors.toList()));
    }
    
    @Test
    void shouldAllowCancelSpecificIterationOfReminder() {
        // data
        DailyRepetition time = new DailyRepetition(TIMESTAMP, 1, Bound.count(7), ZoneOffset.UTC, Set.of(2*DAY+TIMESTAMP, 3*DAY+TIMESTAMP, 5*DAY+TIMESTAMP));
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP+4*DAY, TIMESTAMP+6*DAY), reminder.getTime().schedule(TIMESTAMP+2*DAY, Bound.count(10)).collect(Collectors.toList()));
    }
    
    @Test
    void shouldAllowCancelSpecificIterationOfReminder2() {
        // data
        DailyRepetition time = new DailyRepetition(TIMESTAMP, 1, Bound.count(7), ZoneId.of("Europe/Lisbon"), Set.of(2*DAY+TIMESTAMP, 3*DAY+TIMESTAMP, 5*DAY+TIMESTAMP));
        Reminder reminder = Reminder.Builder.start().withTime(time).build();
        
        // verification
        assertEquals(List.of(TIMESTAMP+4*DAY, TIMESTAMP+6*DAY), reminder.getTime().schedule(TIMESTAMP+2*DAY, Bound.count(10)).collect(Collectors.toList()));
    }
    
    @Test
    void shouldAddAndRetrieveExceptionsToDailyReminder() {
        // data
        DailyRepetition time = new DailyRepetition(60L, 1, Bound.count(3), ZoneOffset.UTC, Set.of(86460L));
        
        // verification
        assertEquals(86460, time.getExceptions().toArray(new Long[1])[0]);
    }
    
    @Test
    void shouldAddAndRetrieveExceptionsToDailyReminder2() {
        // data
        DailyRepetition time = new DailyRepetition(60L, 1, Bound.count(3), ZoneOffset.UTC, Set.of(0L, 86460L, 999999L));
        
        // verification
        assertEquals(86460, time.getExceptions().toArray(new Long[1])[0]);
    }
    
    @Test
    void shouldNotAllowExceptionsIfNotPreviouslyOccurring() {
        // data
        DailyRepetition time = new DailyRepetition(60L, 1, Bound.count(3), ZoneOffset.UTC, Set.of(9999999999L));
        
        // verification
        assertEquals(0, time.getExceptions().size());
    }
    
    @Test
    void shouldAllowNullException() {
        // data
        DailyRepetition time = new DailyRepetition(60L, 1, Bound.count(3), ZoneOffset.UTC, null);
        
        // verication
        assertTrue(time.getExceptions().isEmpty());
    }
    
    @Test
    void shouldNotAllowNullZone() {
        // verification
        assertEquals(1, validator.validateValue(DailyRepetition.class, "zone", null).size());
    }
    
    @Nested
    class AddException {
        @Test
        void shouldAllowAddingException() {
            // data
            DailyRepetition time = new DailyRepetition(60L, 1, Bound.count(3), ZoneOffset.UTC);
            
            // action
            boolean result = time.addException(60L);
            
            // verication
            assertTrue(result);
            assertEquals(Set.of(60L), time.getExceptions());
        }
        
        @Test
        void shouldNotAllowAddingInexistentExceptions() {
             // data
            DailyRepetition time = new DailyRepetition(60L, 1, Bound.count(3), ZoneOffset.UTC);
            
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
        @ValueSource(ints = {0, -1, -10})
        void shouldNotAllowNonPositiveLimitBound(int limit) {
            // data
            Time time = new DailyRepetition(0, 1, Bound.count(limit), ZoneOffset.UTC);
            
            // verification
            assertEquals(1, validator.validate(time).size());
        }
        
        @ParameterizedTest
        @ValueSource(longs = {0, -1, -10})
        void shouldNotAllowNonPositiveTimestampBound(long timestamp) {
            // data
            Time time = new DailyRepetition(0, 1, Bound.timestamp(timestamp), ZoneOffset.UTC);
            
            // verification
            assertEquals(2, validator.validate(time).size());
        }
        
        @ParameterizedTest
        @ValueSource(longs = {3600, 3000})
        void shouldNotAllowBoundTimestampSmallerOrEqualThanStart(long timestamp) {
            // data
            DailyRepetition time = new DailyRepetition(3600L, 1, Bound.timestamp(timestamp), ZoneOffset.UTC);
            
            // verification
            assertEquals(1, validator.validate(time).size());
        }
        
        @Test
        void shouldAllowNullBound() {
            // data
            DailyRepetition time = new DailyRepetition(3600L, 1, null, ZoneOffset.UTC);
            
            // verification
            assertEquals(0, validator.validate(time).size());
            assertEquals(List.of(3600L), time.schedule(0, Bound.count(1)).collect(Collectors.toList()));
        }
    }
    
    @Nested
    class AddStart {
        @ParameterizedTest
        @ValueSource(longs = {-60, -1})
        void shouldNotAllowNegativeStart(long start) {
            // verification
            assertEquals(1, validator.validateValue(DailyRepetition.class, "start", start).size());
        }
    }
    
    @Nested
    class AddStep {
        @ParameterizedTest
        @ValueSource(ints = {0, -1, -10})
        void shouldNotAllowNonPositiveStep(int step) {
            // verification
            assertEquals(1, validator.validateValue(DailyRepetition.class, "step", step).size());
        }
    }
    
    @Nested
    class Truncate {
        @Test
        void shouldRemoveIfTruncationIsBeforeStart() {
            // data
            Time time = new DailyRepetition(3600L, 1, Bound.none(), ZoneOffset.UTC);
            
            // verification
            assertEquals(NoTime.INSTANCE, time.truncate(3600L));
        }
        
        @Test
        void shouldTruncate() {
            // data
            Time time = new DailyRepetition(3600L, 1, Bound.none(), ZoneOffset.UTC);
            
            // action
            Time result1 = time.truncate(3600L + DAY);
            Time result2 = time.truncate(3600L + DAY + 1);
            
            // verification
            assertEquals(List.of(3600L), result1.schedule(0L, Bound.count(3)).collect(Collectors.toList()));
            assertEquals(List.of(3600L, 3600L + DAY), result2.schedule(0L, Bound.count(3)).collect(Collectors.toList()));
        }
        
        @Test
        void shouldRemoveExtraExceptions() {
            // data
            DailyRepetition time = new DailyRepetition(3600L, 1, Bound.none(), ZoneOffset.UTC, Set.of(3600L + DAY, 3600L + 3 * DAY));
            
            // action
            Time result = time.truncate(2 * DAY);
            
            // verification
            assertEquals(time.getStart(), ((DailyRepetition) result).getStart());
            assertEquals(time.getStep(), ((DailyRepetition) result).getStep());
            assertEquals(time.getZone(), ((DailyRepetition) result).getZone());
            assertEquals(Set.of(3600L + DAY), ((DailyRepetition) result).getExceptions());
        }
    }
    
    @Nested
    class Scheduling {
        @Test
        void shouldAllowExtraBInSchedule() {
            // data
            Time time = new DailyRepetition(3600L, 1, Bound.none(), ZoneOffset.UTC);
            
            // action
            Stream<Long> schedule = time.schedule(0L, Bound.count(1));
            
            // verification
            assertEquals(List.of(3600L), schedule.collect(Collectors.toList()));
        }
    }
    
    @Nested
    class GapOverlapDayLightSavings {
        @Test
        void shouldScheduleCorrectlyInGap() {
            // data
            Time time = new DailyRepetition(1616722200, 1, Bound.none(), ZoneId.of("Europe/Lisbon")); // 1h30 daily schedule
            
            // action
            List<Long> schedule = time.schedule(0, Bound.count(4)).collect(Collectors.toList());
            
            // verification
            assertEquals(1616722200, schedule.get(0));
            assertEquals(1616808600, schedule.get(1));
            assertEquals(1616977800, schedule.get(2)); // skips day 28 because gap occurs from 1h to 2h
            assertEquals(1617064200, schedule.get(3));
        }
        
        @Test
        void shouldScheduleCorrectlyInOverlap() {
            // data
            Time time = new DailyRepetition(1635553800, 1, Bound.none(), ZoneId.of("Europe/Lisbon"));
            
            // action
            List<Long> schedule = time.schedule(0, Bound.count(4)).collect(Collectors.toList());
            
            // verification
            assertEquals(1635553800, schedule.get(0));
            assertEquals(1635640200, schedule.get(1));
            assertEquals(1635730200, schedule.get(2));
            assertEquals(1635816600, schedule.get(3));
        }
    }
}
