package org.tdc.util;

public class Util {
	
	public static String spaces(int count) {
		return new String(new char[count]).replace('\0', ' ');
	}
}
