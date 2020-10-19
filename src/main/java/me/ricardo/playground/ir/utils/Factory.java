package me.ricardo.playground.ir.utils;

import java.time.Clock;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class Factory {

	@Produces
	@ApplicationScoped
	public Clock getClock() {
		return Clock.systemUTC();
	}
}
