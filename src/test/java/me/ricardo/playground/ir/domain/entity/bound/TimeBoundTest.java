package me.ricardo.playground.ir.domain.entity.bound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;

class TimeBoundTest {
    
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    @Test
    void shouldHaveSingleTimeBound() {
        assertEquals(List.of(0L, 1L), Bound.timestamp(1L).apply(List.of(0L, 1L, 2L).stream()).collect(Collectors.toList()));
    }
    
    @Test
    void shouldIgnoreIterations() {
        assertEquals(List.of(0L, 1L), Bound.timestamp(1L).apply(List.of(0L, 1L, 2L).stream(), 5L).collect(Collectors.toList()));
    }
    
    @Test
    void shouldBeBounded() {
        assertTrue(Bound.timestamp(1L).isBounded());
    }
    
    @Test
    void shouldNotCreateWithNoneCombined() {
        TimeBound b = Bound.timestamp(1L);
        
        assertEquals(b, b.add(Bound.none()));
    }
    
    @Test
    void shouldRecognizeBeingAfterSomeTime() {
        Bound b = Bound.timestamp(1L);
        
        assertTrue(b.isAfter(0L));
        assertFalse(b.isAfter(1L));
        assertFalse(b.isAfter(2L));
    }
    
    @Test
    void shouldNotAllowNonPositiveTimeBound() {
        assertTrue(validator.validate(Bound.timestamp(1L)).isEmpty());
        assertFalse(validator.validate(Bound.timestamp(0L)).isEmpty());
        assertFalse(validator.validate(Bound.timestamp(-1L)).isEmpty());
    }

    @Test
    void shouldNotAllowAddingNull() throws NoSuchMethodException, SecurityException {
        assertFalse(validator.forExecutables().validateParameters(Bound.timestamp(2L), GuaranteedBound.class.getDeclaredMethod("add", Bound.class), new Object[] {null}).isEmpty());
    }
}
