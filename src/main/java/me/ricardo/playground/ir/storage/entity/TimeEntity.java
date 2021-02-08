package me.ricardo.playground.ir.storage.entity;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import me.ricardo.playground.ir.storage.converter.ZoneConverter;

@Entity(name = "Time")
public class TimeEntity extends PanacheEntity {

    private long timestamp;

    @Column(name = "seconds_in_day")
    private Long secondsSinceStartDay;
    
    public Integer step;
    
    @Enumerated(EnumType.STRING)
    public ChronoUnit unit;
    
    @Convert(converter = ZoneConverter.class)
    private ZoneId zone;
    
    @Column(name = "bound_type")
    @Enumerated(EnumType.STRING)
    public BoundType boundType;
    
    @Column(name = "bound_value")
    public Long boundValue;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="Exception")
    public Set<Long> exceptions;

    
    public long getTimestamp() {
        return timestamp;
    }
    
    public ZoneId getZone() {
        return zone;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        calculateSecondsSinceStartDay();
    }
    
    public void setZone(ZoneId zone) {
        this.zone = zone;
        calculateSecondsSinceStartDay();
    }
    
    private void calculateSecondsSinceStartDay() {
        if (zone != null) {
            // calculation is done removing day light savings offset
            secondsSinceStartDay = (timestamp + zone.getRules().getDaylightSavings(Instant.ofEpochSecond(timestamp)).getSeconds()) % 86400;
        }
    }
}
