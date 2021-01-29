package me.ricardo.playground.ir.domain.utils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

@Priority(1)
@Alternative
public class Factory {

	@Produces
	@ApplicationScoped
	public Clock getClock() {
		return Clock.fixed(Instant.ofEpochSecond(1000L), ZoneOffset.UTC);
	}
}
