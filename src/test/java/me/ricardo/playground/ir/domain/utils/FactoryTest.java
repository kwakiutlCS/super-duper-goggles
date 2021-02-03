package me.ricardo.playground.ir.domain.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import me.ricardo.playground.ir.utils.Factory;

class FactoryTest {

	Factory factory = new Factory();
	
	@Test
	void shouldCreateUTCClock() {
		Clock clock = factory.getClock();
		
		assertEquals(ZoneOffset.UTC, clock.getZone());
	}
}
