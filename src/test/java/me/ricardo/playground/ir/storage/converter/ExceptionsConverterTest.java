package me.ricardo.playground.ir.storage.converter;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

class ExceptionsConverterTest {

    private ExceptionsConverter converter = new ExceptionsConverter();
    
    @Test
    void listOfExceptionsIsConvertedToCommaSeparatedString() {
        assertTrue(Set.of("1,2", "2,1").contains(converter.convertToDatabaseColumn(Set.of(1L, 2L))));
    }
    
    @Test
    void noExceptionIsConvertedToNull() {
        assertNull(converter.convertToDatabaseColumn(Set.of()));
    }
    
    @Test
    void exceptionStringIsConvertedToSetOfExceptions() {
        assertEquals(Set.of(1L, 2L), converter.convertToEntityAttribute("1,2"));
    }
    
    @Test
    void emptyExceptionStringIsConvertedToEmptySetOfExceptions() {
        assertEquals(Set.of(), converter.convertToEntityAttribute(null));
    }
}
