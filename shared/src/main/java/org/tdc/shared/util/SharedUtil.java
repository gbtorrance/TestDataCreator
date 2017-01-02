package org.tdc.shared.util;

/**
 * Static helper members and methods.
 */
public class SharedUtil {
	
	/**
	 * Returns a certain number of spaces.
	 * 
	 * @param count Number of spaces to return.
	 * @return String containing spaces.
	 */
	public static String spaces(int count) {
		return new String(new char[count]).replace('\0', ' ');
	}
	
	/**
	 * Replaces any character in a string that is not a 
	 * letter, number, or underscore with an underscore.
	 * 
	 * <p>This doesn't guaranteed that any resulting folder or file name will be legal;
	 * the various file system rules about legal names are extremely complex, but it's a start.
	 * 
	 * @param name A name that might contain illegal characters.
	 * @return The name with potentially illegal characters stripped out.
	 */
	public static String legalizeName(String name) {
		return name.replaceAll("\\W+", "_");
	}
}
