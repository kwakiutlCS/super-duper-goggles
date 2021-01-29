package me.ricardo.playground.ir.domain.entity;

public record Metadata(long createdAt, long updatedAt) {
    
    public static Metadata of(long createdAt) {
        return new Metadata(createdAt, createdAt);
    }
    
    public static Metadata of(long createdAt, long updatedAt) {
        return new Metadata(createdAt, updatedAt);
    }
}
