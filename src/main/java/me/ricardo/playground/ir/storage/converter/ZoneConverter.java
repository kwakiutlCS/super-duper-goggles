package me.ricardo.playground.ir.storage.converter;

import java.time.ZoneId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ZoneConverter implements AttributeConverter<ZoneId, String> {

    @Override
    public String convertToDatabaseColumn(ZoneId attribute) {
        return attribute.getId();
    }

    @Override
    public ZoneId convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ZoneId.of(dbData);
    }
}
