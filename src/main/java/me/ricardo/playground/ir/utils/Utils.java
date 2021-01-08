package me.ricardo.playground.ir.utils;

public class Utils {

	private Utils() { }
	
	public static long truncateToMinute(long timestamp) {
		return timestamp / 60 * 60;
	}
}
