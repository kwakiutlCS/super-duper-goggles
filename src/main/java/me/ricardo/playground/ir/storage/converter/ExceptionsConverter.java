package me.ricardo.playground.ir.storage.converter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ExceptionsConverter implements AttributeConverter<Set<Long>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Long> exceptions) {
        return exceptions.stream().map(Object::toString).reduce((a, b) -> a+","+b).orElse(null);
    }

    @Override
    public Set<Long> convertToEntityAttribute(String dbData) {
        return dbData == null ? Set.of()
                              : Arrays.stream(dbData.split(",")).map(Long::valueOf).collect(Collectors.toSet());
    }
}
