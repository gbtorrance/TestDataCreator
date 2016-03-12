package org.tdc.util;

/**
 * Static helper methods.
 */
public class Util {
	
	public static String spaces(int count) {
		return new String(new char[count]).replace('\0', ' ');
	}
}
