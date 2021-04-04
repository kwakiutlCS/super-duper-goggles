package me.ricardo.playground.ir.domain.entity.bound;

public sealed interface AtomicBound extends Bound permits NoBound, GuaranteedBound {

    long getValue();
}
