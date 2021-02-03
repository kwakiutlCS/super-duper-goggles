package me.ricardo.playground.ir.domain.entity.bound;

public sealed interface GuaranteedBound extends AtomicBound permits TimeBound, CountBound {

    @Override
    public default boolean isBounded() {
        return true;
    }
    
    @Override
    public default Bound add(Bound other) {
        return other.isBounded() ? new CompositeBound(this, other) : this;
    }
}
