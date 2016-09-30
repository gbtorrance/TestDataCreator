package org.tdc.filter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.tdc.book.Book;
import org.tdc.config.book.FilterConfig;

/**
 * A {@link FilterFactory} implementation.
 */
public class FilterFactoryImpl implements FilterFactory {

	@Override
	public Filter createFilter(FilterConfig config, Book book) {
		String className = config.getFilterClassName();
		Class<?> classy = getClass(className);
		Method buildMethod = getBuildMethod(classy);
		return buildFilter(buildMethod, config, book);
	}

	private Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		}
		catch (Exception ex) {
			throw new RuntimeException("Unable to locate class: " + className, ex);
		}
	}

	private Method getBuildMethod(Class<?> classy) {
		Method buildMethod = null;
		try {
			buildMethod = classy.getMethod("build", FilterConfig.class, Book.class);
		} 
		catch (NoSuchMethodException | SecurityException ex) {
			throwBuildMethodNotFoundException(classy, ex);
		}
		if (!Modifier.isStatic(buildMethod.getModifiers())) {
			throwBuildMethodNotFoundException(classy, null);
		}
		return buildMethod;
	}

	private void throwBuildMethodNotFoundException(Class<?> classy, Exception ex) {
		String message =
				"Class '" + classy.getName() + "' must have a static " + 
				"build(FilterConfig config, Book book) method";
		throw new RuntimeException(message, ex);
	}

	private Filter buildFilter(
			Method buildMethod, FilterConfig config, Book book) {
		
		Object filter = null;
		try {
			filter = buildMethod.invoke(null, config, book);
		} 
		catch (IllegalAccessException e) {
			throw new RuntimeException("Unable to execute static " + 
					"build(FilterConfig config, Book book) " + 
					"method for Filter '" + config.getFilterClassName() + "'", e);
		} 
		catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException().getMessage(), e);
		}
		if (!(filter instanceof Filter)) {
			throw new RuntimeException("Class '" + filter.getClass().getName() + 
					"' must implement Filter interface");
		}
		return (Filter)filter;
	}
}
