package org.tdc.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Static helper methods.
 */
public class Util {
	
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
}
