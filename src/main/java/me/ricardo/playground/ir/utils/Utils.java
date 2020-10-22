package me.ricardo.playground.ir.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Utils {

	public static long truncateToMinute(long timestamp) {
		return timestamp / 60 * 60;
	}
	
	public static int parseMinute(long timestamp, ZoneId zone) {
		return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), zone).getMinute();
	}
	
	public static int parseHour(long timestamp, ZoneId zone) {
		return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), zone).getHour();
	}
}
