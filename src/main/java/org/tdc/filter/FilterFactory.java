package org.tdc.filter;

import org.tdc.book.Book;
import org.tdc.config.book.FilterConfig;

/**
 * Interface defining a factory for creating {@link Filter} instances 
 * based on {@link FilterConfig} parameters.
 */
public interface FilterFactory {
	Filter createFilter(FilterConfig config, Book book);
}
