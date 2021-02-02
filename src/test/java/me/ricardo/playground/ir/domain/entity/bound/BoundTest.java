package me.ricardo.playground.ir.domain.entity.bound;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class BoundTest {
    
    @Test
    void shouldHaveSingleCountBound() {
        assertEquals(List.of(0L), Bound.count(1L).apply(List.of(0L, 1L).stream()).collect(Collectors.toList()));
    }
    
    @Test
    void shouldHaveSingleTimeBound() {
        assertEquals(List.of(0L, 1L), Bound.timestamp(1L).apply(List.of(0L, 1L, 2L).stream()).collect(Collectors.toList()));
    }
    
    @Test
    void shouldHaveNoBound() {
        assertEquals(List.of(0L, 1L, 2L), Bound.none().apply(List.of(0L, 1L, 2L).stream()).collect(Collectors.toList()));
    }
    
    @Test
    void shouldHaveCombinedBound() {
        Bound bound = Bound.none().add(Bound.count(3)).add(Bound.timestamp(3L));
        
        assertEquals(List.of(0L, 1L, 2L), bound.apply(List.of(0L, 1L, 2L, 2L).stream()).collect(Collectors.toList()));
        assertEquals(List.of(0L, 3L), bound.apply(List.of(0L, 3L, 5L).stream()).collect(Collectors.toList()));
    }
    
    @Test
    void shouldHaveIdentityNoBound() {
        Bound b = Bound.count(1L);
        
        assertEquals(b, Bound.none().add(b));
        assertEquals(b, b.add(Bound.none()));
    }
    
    @Test
    void shouldNotCreateCombinedWithNone() {
        Bound b = Bound.count(1L);

        assertEquals(b, Bound.combined(b, Bound.none()));
        assertEquals(b, Bound.combined(Bound.none(), b));
    }
    
    @Test
    void shouldNotCreateOnlyNoneCombined() {
        Bound b = Bound.none();
        
        assertEquals(b, b.add(b));
        assertEquals(b, Bound.combined(b, b));
    }
}
