package me.ricardo.playground.ir.domain.entity.bound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Validation;
import javax.validation.executable.ExecutableValidator;

import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.domain.entity.repetition.NoTime;

class NoBoundTest {
    
    private static final ExecutableValidator validator = Validation.buildDefaultValidatorFactory().getValidator().forExecutables();
    
    @Test
    void shouldHaveNoBound() {
        assertEquals(List.of(0L, 1L, 2L), Bound.none().apply(List.of(0L, 1L, 2L).stream()).collect(Collectors.toList()));
    }
    
    @Test
    void shouldIgnoreIterations() {
        assertEquals(List.of(0L, 1L, 2L), Bound.none().apply(List.of(0L, 1L, 2L).stream(), 5L).collect(Collectors.toList()));
    }
    
    @Test
    void shouldNotBeBounded() {
        assertFalse(Bound.none().isBounded());
    }
    
    @Test
    void shouldNotCreateOnlyNoneCombined() {
        NoBound b = Bound.none();
        
        assertEquals(b, b.add(b));
    }
    
    @Test
    void shouldHaveIdentityNoBound() {
        Bound countBound = Bound.count(1);
        Bound timeBound = Bound.timestamp(1L);
        
        assertEquals(countBound, Bound.none().add(countBound));
        assertEquals(countBound, countBound.add(Bound.none()));
        assertEquals(timeBound, timeBound.add(Bound.none()));
    }
    
    @Test
    void shouldBeAfterAnyTime() {
        assertTrue(Bound.none().isAfter(Long.MAX_VALUE));
    }
    
    @Test
    void shouldNotAllowAddingNull() throws NoSuchMethodException, SecurityException {
        assertFalse(validator.validateParameters(NoBound.INSTANCE, NoBound.class.getDeclaredMethod("add", Bound.class), new Object[] {null}).isEmpty());
    }
}
