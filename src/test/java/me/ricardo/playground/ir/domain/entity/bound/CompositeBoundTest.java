package me.ricardo.playground.ir.domain.entity.bound;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;

class CompositeBoundTest {
    
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    @Test
    void shouldHaveMultipleBounds() {
        Bound bound1 = Bound.count(3).add(Bound.timestamp(3L));
        Bound bound2 = Bound.timestamp(3L).add(Bound.count(3));
        
        assertEquals(List.of(0L, 1L, 2L), bound1.apply(List.of(0L, 1L, 2L, 2L).stream()).collect(Collectors.toList()));
        assertEquals(List.of(0L, 3L), bound1.apply(List.of(0L, 3L, 5L).stream()).collect(Collectors.toList()));
        
        assertEquals(List.of(0L, 1L, 2L), bound2.apply(List.of(0L, 1L, 2L, 2L).stream()).collect(Collectors.toList()));
        assertEquals(List.of(0L, 3L), bound2.apply(List.of(0L, 3L, 5L).stream()).collect(Collectors.toList()));
    }
    
    @Test
    void shouldTakeInAccountForIterations() {
        assertEquals(List.of(), Bound.count(2).add(Bound.timestamp(10L)).apply(List.of(0L, 1L, 2L).stream(), 5L).collect(Collectors.toList()));
        assertEquals(List.of(0L), Bound.count(2).add(Bound.timestamp(10L)).apply(List.of(0L, 1L, 2L).stream(), 1L).collect(Collectors.toList()));
        assertEquals(List.of(0L, 1L), Bound.count(3).add(Bound.timestamp(10L)).apply(List.of(0L, 1L, 2L).stream(), 1L).collect(Collectors.toList()));
    }
    
    @Test
    void shouldBeBounded() {
        assertTrue(Bound.composite(Bound.timestamp(1L), Bound.count(3)).isBounded());
    }
    
    @Test
    void shouldNotCreateWithNoneCombined() {
        NoBound none = Bound.none();
        
        assertEquals(none, Bound.composite(none, none));
    }
    
    @Test
    void shouldAllowMoreThan2Bounds() {
        Bound bound = Bound.count(3).add(Bound.count(2)).add(Bound.count(1));
        
        assertTrue(bound.isBounded());
    }
    
    @Test
    void shouldRecognizeBeingAfterSomeTime() {
        Bound b1 = Bound.timestamp(100L).add(Bound.count(2));
        Bound b2 = Bound.timestamp(50L);
                
        assertTrue(b1.isAfter(75L));
        assertFalse(b2.isAfter(75L));
        assertFalse(b1.add(b2).isAfter(75L));
        assertFalse(b2.add(b1).isAfter(75L));
    }
    
    @Test
    void shouldPropagateValidityFromChildren() {
        Bound b1 = Bound.timestamp(0L);
        Bound b2 = Bound.timestamp(1L);
        
        assertTrue(validator.validate(b2.add(b2)).isEmpty());
        assertFalse(validator.validate(b1.add(b1)).isEmpty());
        assertFalse(validator.validate(b1.add(b2)).isEmpty());
        assertFalse(validator.validate(b2.add(b1)).isEmpty());
    }
}
