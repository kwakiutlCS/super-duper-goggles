package me.ricardo.playground.ir.domain.entity.bound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;

class CountBoundTest {
    
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    @Test
    void shouldHaveSingleCountBound() {
        assertEquals(List.of(0L, 1L), Bound.count(2).apply(List.of(0L, 1L, 2L).stream()).collect(Collectors.toList()));
    }
    
    @Test
    void shouldTakeInAccountForIterations() {
        assertEquals(List.of(), Bound.count(2).apply(List.of(0L, 1L, 2L).stream(), 5L).collect(Collectors.toList()));
        assertEquals(List.of(0L), Bound.count(2).apply(List.of(0L, 1L, 2L).stream(), 1L).collect(Collectors.toList()));
        assertEquals(List.of(0L, 1L), Bound.count(3).apply(List.of(0L, 1L, 2L).stream(), 1L).collect(Collectors.toList()));
    }
    
    @Test
    void shouldBeBounded() {
        assertTrue(Bound.timestamp(1L).isBounded());
    }
    
    @Test
    void shouldNotCreateWithNoneCombined() {
        CountBound b = Bound.count(1);
        
        assertEquals(b, b.add(Bound.none()));
    }
    
    @Test
    void shouldBeAfterAnyTime() {
        assertTrue(Bound.count(1).isAfter(Long.MAX_VALUE));
    }
    
    @Test
    void shouldNotAllowNonPositiveCountBound() {
        assertTrue(validator.validate(Bound.count(1)).isEmpty());
        assertFalse(validator.validate(Bound.count(0)).isEmpty());
        assertFalse(validator.validate(Bound.count(-1)).isEmpty());
    }
    
    @Test
    void shouldNotAllowAddingNull() throws NoSuchMethodException, SecurityException {
        assertFalse(validator.forExecutables().validateParameters(Bound.count(2L), GuaranteedBound.class.getDeclaredMethod("add", Bound.class), new Object[] {null}).isEmpty());
    }
}
