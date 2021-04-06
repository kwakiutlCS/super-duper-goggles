package me.ricardo.playground.ir.domain.entity.bound;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BoundTest {
    
    @Test
    void shouldNotAllowComposeNull() throws NoSuchMethodException, SecurityException {
        Bound b = Bound.count(1L);
        assertThrows(NullPointerException.class, () -> Bound.composite(null, b));
        assertThrows(NullPointerException.class, () -> Bound.composite(b, null));
    }
}
