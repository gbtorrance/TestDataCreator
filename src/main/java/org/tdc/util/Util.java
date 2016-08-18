package org.tdc.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.format.DateTimeFormatter;

/**
 * Static helper members and methods.
 */
public class Util {
	
	public static final DateTimeFormatter EXPORT_DATE_TIME_FORMATTER = 
			DateTimeFormatter.ofPattern("yyMMdd_kkmmss_SSS");
	
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

	/**
	 * Extracts a "property" value from an object using Reflection. 
	 * The property name prefixed with "get" indicates the name of the method to call.
	 * 
	 * <p>If the property does not exist for the given object, the default value will be returned.
	 * If the property does exist, but there is an error when calling the corresponding method,
	 * an exception will be thrown.
	 * 
	 * <p>To allow for extracting properties that have corresponding methods that do not begin
	 * with "get", the full method name can be specified as the property name (e.g. "hasChild").
	 * 
	 * @param object Object from which the property will be extracted. 
	 * @param propertyName Property name.
	 * @param defaultValue Default value (if the object does not have the given property).
	 * @return Property value (or default value, if the property does not exist).
	 */
	public static String getStringValueFromProperty(Object object, String propertyName, String defaultValue) {
		String value = defaultValue;

		Method method = null;
		try {
			// check if full method name was provided 
			method = object.getClass().getMethod(propertyName);
		}
		catch (NoSuchMethodException | SecurityException e) {
			// no such method found; no problem
		}
		try {
			// check if property name (without "get" prefix) was provided 
			String methodName = 
					"get" + 
					propertyName.substring(0, 1).toUpperCase() + 
					(propertyName.length() > 1 ? propertyName.substring(1, propertyName.length()) : "");
			method = object.getClass().getMethod(methodName);
		}
		catch (NoSuchMethodException | SecurityException e) {
			// no such method found; no problem
		}
		try {
			if (method != null) {
				Object result = method.invoke(object);
				value = result.toString();
			}
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Unable to call property '" + propertyName + "'", e);
		}
		return value;
	}
	
	/**
	 * Purges the content of a directory of any files or sub-directories.
	 * 
	 * @param dir Path of directory to purge
	 */
	public static void purgeDirectory(Path dirToPurge) {
		if (Files.isDirectory(dirToPurge)) {
			try {
				Files.walkFileTree(dirToPurge, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}
					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						if (!Files.isSameFile(dirToPurge, dir)) {
							Files.delete(dir);
						}
						return FileVisitResult.CONTINUE;
					}
				});
			} 
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
